package com.oceantech.tracking.ui.security

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.databinding.FragmentLoginBinding
import com.oceantech.tracking.ui.MainActivity
import javax.inject.Inject
//done
class LoginFragment @Inject constructor() : TrackingBaseFragment<FragmentLoginBinding>() {
    private val viewModel: SecurityViewModel by activityViewModel()

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        listenEvent()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun listenEvent() {
        views.loginSubmit.setOnClickListener {
            loginSubmit()
        }
        views.labelSigin.setOnClickListener {
            viewModel.handleReturnInfoRegister()
        }
        views.labelResetPassword.setOnClickListener {
            viewModel.handleReturnResetPass()
        }
        views.password.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                views.passwordTil.error =null
            }

        })
    }

    private fun loginSubmit() {
        val username = views.username.text.toString().trim()
        val password = views.password.text.toString().trim()

        if (validateLoginInput(username, password)) {
            viewModel.handle(SecurityViewAction.LoginAction(username, password))
        }
    }

    private fun validateLoginInput(username: String, password: String): Boolean {
        var isValid = true

        if (username.isEmpty()) {
            views.usernameTil.error = getString(R.string.username_not_empty)
            isValid = false
        } else {
            views.usernameTil.error = null
        }

        if (password.isEmpty()) {
            views.passwordTil.error = getString(R.string.password_not_empty)
            isValid = false
        } else {
            views.passwordTil.error = null
        }

        return isValid
    }

    override fun invalidate(): Unit = withState(viewModel) {
        when (it.asyncLogin) {
            is Success -> {
                it.asyncLogin.invoke()?.let { token ->
                    val sessionManager = context?.let { ctx -> SessionManager(ctx.applicationContext) }
                    token.accessToken?.let { token -> sessionManager!!.saveAuthToken(token) }
                    token.refreshToken?.let { refreshToken -> sessionManager!!.saveAuthTokenRefresh(refreshToken) }
                }
                Toast.makeText(requireContext(), getString(R.string.login_success), Toast.LENGTH_LONG).show()
                startActivity(Intent(requireContext(), MainActivity::class.java))
                activity?.finish()
            }
            is Fail -> {
                views.passwordTil.error = getString(R.string.login_fail)
            }
        }
    }
}
