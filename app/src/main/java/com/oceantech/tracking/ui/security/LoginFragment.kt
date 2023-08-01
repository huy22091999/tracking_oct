package com.oceantech.tracking.ui.security

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.viewbinding.ViewBinding
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.google.firebase.messaging.FirebaseMessaging
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.databinding.DialogLoginBinding
import com.oceantech.tracking.databinding.FragmentLoginBinding
import com.oceantech.tracking.ui.MainActivity
import com.oceantech.tracking.ui.home.HomeViewAction
import com.oceantech.tracking.utils.initialAlertDialog
import com.oceantech.tracking.utils.isNetworkAvailable
import javax.inject.Inject

class LoginFragment @Inject constructor() : TrackingBaseFragment<FragmentLoginBinding>() {
    private val viewModel: SecurityViewModel by activityViewModel()
    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater,container,false)
    }
    lateinit var username:String
    lateinit var password:String
    private var hasShowDialog = false
    private lateinit var dialog:AlertDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        views.loginSubmit.setOnClickListener {
            loginSubmit()
        }
        views.labelSigin.setOnClickListener {
            viewModel.handleRemoveStateError()
            views.usernameTil.error = null
            views.passwordTil.error = null
            viewModel.handleReturnSignin()
        }
        views.labelResetPassword.setOnClickListener {
            viewModel.handleRemoveStateError()
            views.usernameTil.error = null
            views.passwordTil.error = null
            viewModel.handleReturnResetPass()
        }
        dialog = initialAlertDialog(requireContext(),
            returnSignIn, refuseReturnSignIn,
            requireContext().getString(R.string.login_error),
            requireContext().getString(R.string.register),
            requireContext().getString(R.string.back)
        )
        super.onViewCreated(view, savedInstanceState)
    }

    private fun loginSubmit()
    {
        if(isNetworkAvailable(requireContext())){
            username=views.username.text.toString().trim()
            password=views.password.text.toString().trim()
            if(username.isNullOrEmpty()) views.usernameTil.error=getString(R.string.username_not_empty)
            if(password.isNullOrEmpty()) views.passwordTil.error=getString(R.string.username_not_empty)
            if (!username.isNullOrEmpty()&&!password.isNullOrEmpty())
            {
                viewModel.handle(SecurityViewAction.LogginAction(username, password))
            }
        } else {
            Toast.makeText(requireContext(), getString(R.string.network_disconnected),Toast.LENGTH_SHORT).show()
        }
    }

    private val returnSignIn:()->Unit={
        viewModel.handleRemoveStateError()
        viewModel.handleReturnSignin()
    }

    private val refuseReturnSignIn:()->Unit={
        hasShowDialog = true
        viewModel.handleRemoveStateError()
        views.usernameTil.error = null
        views.passwordTil.error = null
    }

    override fun onPause() {
        super.onPause()
        if(dialog != null || dialog.isShowing){
            dialog.dismiss()
        }
    }

    override fun invalidate(): Unit = withState(viewModel){
        when(it.asyncLogin){
            is Success ->{
                it.asyncLogin.invoke()?.let { token->
                    val sessionManager = context?.let { it1 -> SessionManager(it1.applicationContext) }
                    token.accessToken?.let { it1 -> sessionManager!! .saveAuthToken(it1) }
                    token.refreshToken?.let { it1 -> sessionManager!!.saveAuthTokenRefresh(it1) }
                    viewModel.handle(SecurityViewAction.SaveTokenAction(token!!))
                }

                Toast.makeText(requireContext(),getString(R.string.login_success),Toast.LENGTH_LONG).show()
                dismissLoadingDialog()
                startActivity(Intent(requireContext(), MainActivity::class.java))
                activity?.finish()
            }
            is Loading ->{
                showLoadingDialog()
            }
            is Fail->{
                views.passwordTil.error=getString(R.string.login_fail)
                views.passwordTil.editText?.text?.clear()
                dialog.show()
                dismissLoadingDialog()
            }
            is Uninitialized -> {
                dialog.dismiss()
            }
        }

        when(it.asyncTokenDevice){
            is Success -> {

            }
        }
    }
}