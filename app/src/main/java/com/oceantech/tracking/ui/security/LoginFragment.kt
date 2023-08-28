package com.oceantech.tracking.ui.security

import android.content.Intent
import android.os.Bundle
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
import com.oceantech.tracking.utils.handleLogOut
import javax.inject.Inject

class LoginFragment @Inject constructor() : TrackingBaseFragment<FragmentLoginBinding>() {
    private val viewModel: SecurityViewModel by activityViewModel()

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        views.loginSubmit.setOnClickListener {
            loginSubmit()
        }
        views.labelSigin.setOnClickListener {
            viewModel.handleReturnInforRegister()
        }
        views.labelResetPassword.setOnClickListener {
            viewModel.handleReturnResetPass()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun loginSubmit() {
        val username = views.username.text.toString().trim()
        val password = views.password.text.toString().trim()

        if (validateLoginInput(username, password)) {
            viewModel.handle(SecurityViewAction.LogginAction(username, password))
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
                    viewModel.handle(SecurityViewAction.SaveTokenAction(token))
                }
                Toast.makeText(requireContext(), getString(R.string.login_success), Toast.LENGTH_LONG).show()
                startActivity(Intent(requireContext(), MainActivity::class.java))
                activity?.finish()
            }
            is Fail -> {
                if(it.asyncLogin.invoke().toString().isNotEmpty()){
                    requireActivity().handleLogOut()
                }
                views.passwordTil.error = getString(R.string.login_fail)
            }
        }
    }
}
