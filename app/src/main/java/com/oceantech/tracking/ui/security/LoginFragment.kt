package com.oceantech.tracking.ui.security

import android.content.Context
import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.google.firebase.messaging.FirebaseMessaging
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.TokenResponse
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.databinding.FragmentLoginBinding
import com.oceantech.tracking.ui.MainActivity
import com.oceantech.tracking.utils.changeDarkMode
import com.oceantech.tracking.utils.checkError
import com.oceantech.tracking.utils.registerNetworkReceiver
import com.oceantech.tracking.utils.unregisterNetworkReceiver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class LoginFragment @Inject constructor() : TrackingBaseFragment<FragmentLoginBinding>() {
    private val viewModel: SecurityViewModel by activityViewModel()

    @Inject
    lateinit var sessionManager: SessionManager
    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater, container, false)
    }

    lateinit var username: String
    lateinit var password: String

    private var stateLogin = 0

    companion object {
        private const val LOGIN = 1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeDarkMode(sessionManager.getDarkMode())
        views.password.doOnTextChanged { text, start, before, count ->
            views.passwordTil.isErrorEnabled = false
        }
        views.userName.doOnTextChanged { text, start, before, count ->
            views.usernameTil.isErrorEnabled = false
        }

        views.loginSubmit.setOnClickListener {
            val inputMethod = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethod.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)

            registerNetworkReceiver {
                // make login works in main thread to prevent crashing from username or pass is empty
                GlobalScope.launch(Dispatchers.Main) {
                    loginSubmit()
                }
            }
        }
        views.labelSigin.setOnClickListener {
            viewModel.handleReturnSignin()
        }
        views.labelResetPassword.setOnClickListener {
            viewModel.handleReturnResetPass()
        }
        viewModel.onEach {
            views.loginPB.isVisible = it.asyncLogin is Loading
            views.passwordTil.isErrorEnabled = it.asyncLogin is Fail
        }
    }

    private fun loginSubmit() {
        username = views.userName.text.toString().trim()
        password = views.password.text.toString().trim()
        if (username.isNullOrEmpty()) views.usernameTil.error =
            getString(R.string.username_not_empty)
        if (password.isNullOrEmpty()) views.passwordTil.error =
            getString(R.string.username_not_empty)
        if (!username.isNullOrEmpty() && !password.isNullOrEmpty()) {
            viewModel.handle(SecurityViewAction.LogginAction(username, password))
            stateLogin = LOGIN
        }
    }


    override fun invalidate(): Unit = withState(viewModel) {
        when(stateLogin) {
            LOGIN -> handleLogin(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterNetworkReceiver()
    }

    private fun handleLogin(state: SecurityViewState){
        when (state.asyncLogin) {
            is Success -> {
                state.asyncLogin.invoke()?.let { token ->
                    val sessionManager =
                        context?.let { it1 -> SessionManager(it1.applicationContext) }
                    token.accessToken?.let { it1 -> sessionManager!!.saveAuthToken(it1) }
                    token.refreshToken?.let { it1 -> sessionManager!!.saveAuthTokenRefresh(it1) }
                    Timber.tag("Login").i(token.toString())
                    viewModel.handle(SecurityViewAction.SaveTokenAction(token))
                }
                Toast.makeText(
                    requireContext(),
                    getString(R.string.login_success),
                    Toast.LENGTH_LONG
                ).show()
                startActivity(Intent(requireContext(), MainActivity::class.java))
                activity?.finish()
            }

            is Fail -> {
                state.asyncLogin.error.message?.let { it1 ->
                    checkError(it1)
                    Log.i("Login", it1)

                }
                views.passwordTil.error = getString(R.string.login_fail)
            }

            else -> {}
        }
    }
}