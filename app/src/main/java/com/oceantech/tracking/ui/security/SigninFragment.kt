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
import com.oceantech.tracking.data.network.getDeviceToken
import com.oceantech.tracking.databinding.FragmentSigninBinding
import com.oceantech.tracking.utils.DialogUtil
import com.oceantech.tracking.utils.PasswordMaskUtil
import com.oceantech.tracking.utils.addEndIconClickListener
import com.oceantech.tracking.utils.isNetworkAvailable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class SigninFragment @Inject constructor() : TrackingBaseFragment<FragmentSigninBinding>() {
    private val viewModel: SecurityViewModel by activityViewModel()
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSigninBinding {
        return FragmentSigninBinding.inflate(inflater, container, false)
    }

    lateinit var username: String
    lateinit var password: String
    lateinit var confirmPassword: String
    lateinit var last_name: String
    lateinit var first_name: String
    lateinit var display_name: String
    lateinit var email: String
    lateinit var tokenDevice:String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        views.password.transformationMethod = PasswordMaskUtil.getInstance()
        views.passwordTil.addEndIconClickListener()
        views.confirmPassword.transformationMethod = PasswordMaskUtil.getInstance()
        views.confirmPasswordTil.addEndIconClickListener()
        views.send.setOnClickListener {
            signSubmit()
        }

    }

    private fun signSubmit() {
        username = views.username.text.toString().trim()
        password = views.password.text.toString().trim()
        confirmPassword = views.confirmPassword.text.toString().trim()
        last_name = views.lastName.text.toString().trim()
        first_name = views.firstName.text.toString().trim()
        display_name = views.displayName.text.toString().trim()
        email = views.email.text.toString().trim()

        if (username.isNullOrEmpty()) views.usernameTil.error =
            getString(R.string.username_not_empty)
        if (password.isNullOrEmpty()) views.passwordTil.error =
            getString(R.string.username_not_empty)
        if (confirmPassword.isNullOrEmpty()) views.confirmPasswordTil.error =
            getString(R.string.username_not_empty)
        if (last_name.isNullOrEmpty()) views.lastNameTil.error =
            getString(R.string.username_not_empty)
        if (first_name.isNullOrEmpty()) views.firstNameTil.error =
            getString(R.string.username_not_empty)
        if (display_name.isNullOrEmpty()) views.displayNameTil.error =
            getString(R.string.username_not_empty)
        if (email.isNullOrEmpty()) views.emailTil.error =
            getString(R.string.username_not_empty)
        if(password!=confirmPassword) views.confirmPassword.error=getString(R.string.confirm_password_fail)

        if (!username.isNullOrEmpty() && !password.isNullOrEmpty() && !confirmPassword.isNullOrEmpty() &&
            !last_name.isNullOrEmpty() && !first_name.isNullOrEmpty() && !display_name.isNullOrEmpty() &&
            !email.isNullOrEmpty() && password==confirmPassword
        ) {
            DialogUtil.showLoadingDialog(requireContext())
            CoroutineScope(Dispatchers.Main).launch {
                val token = getDeviceToken(requireContext())
                if (token != null) {
                    tokenDevice = token
                    val newUser = User(
                        active=true,
                        username = username,
                        password = password,
                        confirmPassword = confirmPassword,
                        displayName = display_name,
                        email = email,
                        firstName = first_name,
                        lastName = last_name,
                        tokenDevice=tokenDevice

                        // Các trường khác mà bạn muốn sử dụng
                    )

                    if(isNetworkAvailable(requireContext())){
                        viewModel.handle(
                            SecurityViewAction.SignAction(newUser)
                        )
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.network_disconnected),Toast.LENGTH_SHORT).show()
                    }
                }
            }
//            val user = User(
//                active=true,
//                username = username,
//                password = password,
//                confirmPassword = confirmPassword,
//                displayName = display_name,
//                email = email,
//                firstName = first_name,
//                lastName = last_name,
//                tokenDevice=tokenDevice
//                // Các trường khác mà bạn muốn sử dụng
//            )
//            // Call ViewModel to perform registration
//            //DialogUtil.showLoadingDialog(requireContext())
//            viewModel.handle(
//                SecurityViewAction.SignAction(user)
//            )
        }
    }

    override fun invalidate():Unit= withState(viewModel) {
        when(it.asyncRegister){
            is Success ->{
                Toast.makeText(
                    requireContext(),
                    getString(R.string.sign_success),
                    Toast.LENGTH_LONG
                ).show()
                DialogUtil.hideLoading()
                activity?.supportFragmentManager?.popBackStack()

            }
            is Fail -> {
                views.emailTil.error = getString(R.string.sign_fail)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.sign_fail),
                    Toast.LENGTH_LONG
                ).show()
                DialogUtil.hideLoading()
            }

            else -> {}
        }
    }
}