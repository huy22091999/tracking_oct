package com.oceantech.tracking.ui.security

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
import com.oceantech.tracking.data.model.Role
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.FragmentSigninBinding
import javax.inject.Inject


class SigninFragment @Inject constructor() : TrackingBaseFragment<FragmentSigninBinding>() {

    private val viewModel: SecurityViewModel by activityViewModel()
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSigninBinding {
        return FragmentSigninBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        views.send.setOnClickListener {
            val username = views.username.text.toString().trim()
            val displayName = views.displayName.text.toString().trim()
            val email = views.email.text.toString().trim()
            val firstName = views.firstName.text.toString().trim()
            val lastName = views.lastName.text.toString().trim()
            val password = views.password.text.toString().trim()
            validateData(username, displayName, email, firstName, lastName, password)
        }
        views.tvReturnLogin.setOnClickListener { viewModel.handleReturnLogin() }
    }

    private fun validateData(
        username: String,
        displayName: String,
        email: String,
        firstName: String,
        lastName: String,
        password: String
    ) {
        when {
            lastName.isEmpty() -> views.lastNameTil.error = getString(R.string.username_not_empty)
            firstName.isEmpty() -> views.firstNameTil.error = getString(R.string.username_not_empty)
            displayName.isEmpty() -> views.displayNameTil.error = getString(R.string.username_not_empty)
            email.isEmpty() -> views.emailTil.error = getString(R.string.username_not_empty)
            username.isEmpty() -> views.usernameTil.error = getString(R.string.username_not_empty)
            password.isEmpty() -> views.passwordTil.error = getString(R.string.username_not_empty)
            else -> signIn(username,displayName,email,firstName,lastName,password)
        }
    }

    private fun signIn(username: String, displayName: String, email: String, firstName: String, lastName: String, password: String) {
        val user = User(
            null, username, true, null, false,
            null, displayName, null, email,
            firstName, lastName, password, false, listOf(Role(4)), null,
            4, null, false
        )
        viewModel.handle(SecurityViewAction.SignIn(user))
    }

    override fun invalidate() : Unit = withState(viewModel){
        when(it.asyncSignIn){
            is Success -> {
                Toast.makeText(context,context?.getString(R.string.sign_in_success),Toast.LENGTH_LONG).show()
                viewModel.handleReturnLogin()
            }
            is Fail -> {
                Toast.makeText(context,context?.getString(R.string.sign_in_false),Toast.LENGTH_LONG).show()
            }
        }
    }
}