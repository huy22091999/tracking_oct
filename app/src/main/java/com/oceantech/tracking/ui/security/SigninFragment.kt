package com.oceantech.tracking.ui.security

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
import com.oceantech.tracking.databinding.FragmentSigninBinding
import com.oceantech.tracking.utils.checkError
import com.oceantech.tracking.utils.toIsoInstant


class SigninFragment : TrackingBaseFragment<FragmentSigninBinding>() {

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
            val userName = views.username.text.toString()
            val password = views.password.text.toString()
            val displayName = views.displayName.text.toString()
            val firstName = views.firstName.text.toString()
            val lastName = views.lastName.text.toString()
            val gender = views.gender.text.toString()
            val dob = toIsoInstant(views.dob.text.toString())
            val email = views.email.text.toString()
            val university = views.university.text.toString()
            val year = views.studentYear.text.toString().toInt()
            val confirmPassword = views.rePassword.text.toString()

            handleSignIn(
                userName,
                password,
                displayName,
                firstName,
                lastName,
                gender,
                dob,
                email,
                university,
                year,
                confirmPassword
            )

        }
    }

    override fun invalidate(): Unit = withState(viewModel) {
        when (it.userSignIn) {
            is Success -> {
                Toast.makeText(requireContext(), "Register Successfully", Toast.LENGTH_SHORT).show()
                parentFragmentManager.beginTransaction().replace(R.id.frame_layout, LoginFragment())
                    .commit()
            }

            is Fail -> {
                Log.i("SignIn", it.userSignIn.error.toString())
                Toast.makeText(requireContext(), "Register failed", Toast.LENGTH_SHORT).show()
            }

            else -> {}
        }
    }

    private fun handleSignIn(
        userName: String,
        password: String,
        displayName: String,
        firstName: String,
        lastName: String,
        gender: String,
        dob: String,
        email: String,
        university: String,
        year: Int,
        confirmPassword: String
    ) {
        viewModel.handle(
            SecurityViewAction.SignInAction(
                userName,
                password,
                displayName,
                firstName,
                lastName,
                gender, dob, email, university,
                year,
                confirmPassword
            )
        )
    }
}