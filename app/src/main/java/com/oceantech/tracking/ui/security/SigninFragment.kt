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
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.databinding.DialogLoginBinding
import com.oceantech.tracking.databinding.FragmentSigninBinding
import com.oceantech.tracking.ui.MainActivity
import com.oceantech.tracking.utils.validateEmail
import javax.inject.Inject


class SigninFragment @Inject constructor() : TrackingBaseFragment<FragmentSigninBinding>() {
    val viewModel:SecurityViewModel by activityViewModel()
    lateinit var username:String
    lateinit var displayName:String
    lateinit var email:String
    lateinit var firstName:String
    lateinit var lastName:String
    lateinit var password:String
    lateinit var birthPlace:String
    lateinit var university:String
    lateinit var year:String
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSigninBinding {
        return FragmentSigninBinding.inflate(inflater,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        views.send.setOnClickListener {
            send()
        }
        views.labelSignup.setOnClickListener {
            viewModel.handleReturnLogin()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun send() {
        username = views.username.text.toString().trim()
        displayName = views.displayName.text.toString().trim()
        email = views.email.text.toString().trim()
        firstName = views.firstName.text.toString().trim()
        lastName = views.lastName.text.toString().trim()
        password = views.password.text.toString().trim()
        birthPlace = views.birthPlace.text.toString().trim()
        university = views.university.text.toString().trim()
        year = views.year.text.toString().trim()

        if(username.isNullOrEmpty()) views.username.error=getString(R.string.username_not_empty)
        if(displayName.isNullOrEmpty()) views.displayName.error=getString(R.string.username_not_empty)
        if(email.isNullOrEmpty() && !validateEmail(email)) views.email.error=getString(R.string.username_not_empty)
        if(firstName.isNullOrEmpty()) views.firstName.error=getString(R.string.username_not_empty)
        if(lastName.isNullOrEmpty()) views.lastName.error=getString(R.string.username_not_empty)
        if(password.isNullOrEmpty()) views.password.error=getString(R.string.username_not_empty)
        if(birthPlace.isNullOrEmpty()) views.birthPlace.error=getString(R.string.username_not_empty)
        if(university.isNullOrEmpty()) views.university.error=getString(R.string.username_not_empty)
        if(year.isNullOrEmpty()) views.year.error = getString(R.string.username_not_empty)
        if(!username.isNullOrEmpty() && !displayName.isNullOrEmpty()
            && !email.isNullOrEmpty() && validateEmail(email)
            && !firstName.isNullOrEmpty() && !lastName.isNullOrEmpty()
            && !password.isNullOrEmpty() && !birthPlace.isNullOrEmpty()
            && !university.isNullOrEmpty() && !year.isNullOrEmpty()){
            viewModel.handle(SecurityViewAction.SignAction(username, displayName, email, firstName, lastName, password, birthPlace, university, year.toInt()))
        }
    }

    private fun createDialog(): AlertDialog {
        val builder = AlertDialog.Builder(requireContext())
        val view: ViewBinding = DialogLoginBinding.inflate(LayoutInflater.from(requireContext()))
        builder.setView(view.root)

        val alertDialog = builder.create()

        with(view as DialogLoginBinding){
            view.dialogTitle.text = getString(R.string.signin_success_confirm)
            view.returnSignIn.text = getString(R.string.ok)
            view.returnSignIn.setOnClickListener {
                alertDialog.dismiss()
                viewModel.handleReturnLogin()
            }
            view.back.setOnClickListener {
                alertDialog.dismiss()
            }
        }
        return alertDialog
    }

    override fun invalidate():Unit = withState(viewModel){
        when(it.asyncSign){
            is Success ->{
                Log.i("Test Sign", "Success $username - $password")
                Toast.makeText(requireActivity(),getString(R.string.sign_success), Toast.LENGTH_SHORT).show()
                dismissLoadingDialog()
                createDialog().show()
            }
            is Loading ->{
                showLoadingDialog()
            }
            is Fail ->{
                Log.e("Test Sign", "Fail")
                dismissLoadingDialog()
            }
        }
    }
}