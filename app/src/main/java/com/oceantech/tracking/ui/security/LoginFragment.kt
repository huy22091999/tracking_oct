package com.oceantech.tracking.ui.security

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.oceantech.tracking.data.model.TokenResponse
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.databinding.FragmentLoginBinding
import com.oceantech.tracking.ui.MainActivity
import javax.inject.Inject


class LoginFragment @Inject constructor() : TrackingBaseFragment<FragmentLoginBinding>() {
    private val viewModel:SecurityViewModel by activityViewModel()
    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater,container,false)
    }
    lateinit var username:String
    lateinit var password:String
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        views.loginSubmit.setOnClickListener {
            loginSubmit()
        }
        views.labelSigin.setOnClickListener {
            viewModel.handleReturnSignin()
        }
        views.labelResetPassword.setOnClickListener {
            viewModel.handleReturnResetPass()
        }
        super.onViewCreated(view, savedInstanceState)
    }
    private fun loginSubmit()
    {
        username=views.userName.text.toString().trim()
        password=views.password.text.toString().trim()
        if(username.isNullOrEmpty()) views.usernameTil.error=getString(R.string.username_not_empty)
        if(password.isNullOrEmpty()) views.passwordTil.error=getString(R.string.username_not_empty)
        if (!username.isNullOrEmpty()&&!password.isNullOrEmpty())
        {
            viewModel.handle(SecurityViewAction.LogginAction(username,password))
        }
    }

    override fun invalidate(): Unit = withState(viewModel){
        when(it.asyncLogin){
            is Success ->{
                it.asyncLogin.invoke()?.let { token->
                    val sessionManager = context?.let { it1 -> SessionManager(it1.applicationContext) }
                    token.accessToken?.let { it1 -> sessionManager!! .saveAuthToken(it1) }
                    token.refreshToken?.let { it1 -> sessionManager!!.saveAuthTokenRefresh(it1) }
                    Log.i("Login", token.toString())
                    viewModel.handle(SecurityViewAction.SaveTokenAction(token))
                }
                Toast.makeText(requireContext(),getString(R.string.login_success),Toast.LENGTH_LONG).show()
                Log.i("Login", it.asyncLogin.toString())
                startActivity(Intent(requireContext(), MainActivity::class.java))
                activity?.finish()
            }
            is Fail->{
                Log.i("Login", (it.asyncLogin as Fail<TokenResponse>).error.toString())
                views.passwordTil.error=getString(R.string.login_fail)
            }
        }

    }
}