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
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.FragmentSigninBinding
//done
class SignUpFragment : TrackingBaseFragment<FragmentSigninBinding>() {

    private val viewModel: SecurityViewModel by activityViewModel()
    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSigninBinding {
        return FragmentSigninBinding.inflate(inflater, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = arguments?.getSerializable("info_user") as User
        views.send.setOnClickListener {
            signupSubmit(user)
        }
    }

    private fun signupSubmit(user: User) {
        val displayName = views.displayName.text.toString()
        val username = views.username.text.toString()
        val email = views.email.text.toString()
        val password = views.password.text.toString()
        val confirmPassword = views.confirmPassword.text.toString()

        if (validateSignupInput(displayName, username, email, password, confirmPassword)) {
            user.apply {
                this.displayName = displayName
                this.username = username
                this.email = email
                this.password = password
                this.confirmPassword = confirmPassword
            }
            viewModel.handle(SecurityViewAction.SignupAction(user))
        }
    }

    private fun validateSignupInput(
        displayName: String,
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        var isValid = true

        if (displayName.isEmpty()) {
            views.displayNameTil.error = getString(R.string.display_name_not_empty)
            isValid = false
        } else {
            views.displayNameTil.error = null
        }

        if (username.isEmpty()) {
            views.usernameTil.error = getString(R.string.username_not_empty)
            isValid = false
        } else {
            views.usernameTil.error = null
        }

        if (email.isEmpty()) {
            views.emailTil.error = getString(R.string.email_not_empty)
            isValid = false
        } else {
            views.emailTil.error = null
        }

        if (password.isEmpty()) {
            views.passwordTil.error = getString(R.string.password_not_empty)
            isValid = false
        } else {
            views.passwordTil.error = null
        }

        if (confirmPassword.isEmpty()) {
            views.confirmPasswordTil.error = getString(R.string.confirm_password_not_empty)
            isValid = false
        } else if (confirmPassword != password) {
            views.confirmPasswordTil.error = getString(R.string.password_not_match)
            isValid = false
        } else {
            views.confirmPasswordTil.error = null
        }

        return isValid
    }

    override fun invalidate(): Unit = withState(viewModel) {
        when (it.userCurrent) {
            is Success -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.signup_success),
                    Toast.LENGTH_LONG
                ).show()
                viewModel.handleReturnLogin()
            }

            is Fail -> {
                Toast.makeText(activity, "err", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
