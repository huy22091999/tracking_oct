package com.oceantech.tracking.ui.security

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.network.getDeviceToken
import com.oceantech.tracking.databinding.FragmentNextSigninBinding
import com.oceantech.tracking.ui.security.NextSigninFragmentArgs
import com.oceantech.tracking.utils.validateEmail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NextSigninFragment : TrackingBaseFragment<FragmentNextSigninBinding>() {
    private val viewModel: SecurityViewModel by activityViewModel()
    private lateinit var user:User
    private val arg: NextSigninFragmentArgs by navArgs()
    lateinit var displayName:String
    lateinit var email:String
    lateinit var userName:String
    lateinit var password:String
    lateinit var confirmPassword:String
    lateinit var gender:String
    lateinit var firstName:String
    lateinit var lastName:String
    lateinit var university:String
    lateinit var birthPlace:String
    lateinit var year:String
    lateinit var tokenDevice:String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = arg.userReceive

        views.send.setOnClickListener {
            register()
        }

        views.labelSignup.setOnClickListener {
            viewModel.handleReturnLogin()
        }
    }

    private fun register() {
        displayName = views.displayName.text.toString().trim()
        email = views.email.text.toString().trim()
        userName = views.username.text.toString().trim()
        password = views.password.text.toString().trim()
        confirmPassword = views.confrimPassword.text.toString().trim()
        gender = user.gender.toString()
        firstName = user.firstName.toString()
        lastName = user.lastName.toString()
        university = user.university.toString()
        birthPlace = user.birthPlace.toString()
        year = user.year.toString()


        if(displayName.isNullOrEmpty()) views.displayName.error = requireContext().getString(R.string.username_not_empty)
        if(!validateEmail(email)) views.email.error = requireContext().getString(R.string.email_not_correct)
        if(userName.isNullOrEmpty()) views.username.error = requireContext().getString(R.string.username_not_empty)
        if(password.isNullOrEmpty()) views.password.error = requireContext().getString(R.string.username_not_empty)
        if(confirmPassword.isNullOrEmpty()) views.confrimPassword.error = requireContext().getString(R.string.username_not_empty)
        if (password != confirmPassword) views.confrimPassword.error = requireContext().getString(R.string.confirm_password_not_correct)
        if (!displayName.isNullOrEmpty() || validateEmail(email) || userName.isNullOrEmpty() || password.isNullOrEmpty() || confirmPassword.isNullOrEmpty()){
            CoroutineScope(Dispatchers.Main).launch {
                val token = getDeviceToken(requireContext())
                if (token != null) {
                    tokenDevice = token
                    val newUser = User(null,
                        userName,
                        true,
                        birthPlace,
                        false,
                        confirmPassword,
                        0,
                        0,
                        displayName,
                        null,
                        email,
                        firstName,
                        gender,
                        true,
                        lastName,
                        password,
                        null,
                        listOf(),
                        tokenDevice,
                        university,
                        year.toInt()
                    )

                    viewModel.handle(
                        SecurityViewAction.SignAction(newUser)
                    )
                }
            }
        }
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNextSigninBinding = FragmentNextSigninBinding.inflate(inflater, container, false)


    override fun invalidate():Unit = withState(viewModel){
        when(it.asyncSign){
            is Success ->{
                Log.i("Test Sign", "Success $userName - $password")
                Toast.makeText(requireActivity(),getString(R.string.sign_success), Toast.LENGTH_SHORT).show()
                dismissLoadingDialog()
                viewModel.handleReturnLogin()
            }
            is Loading ->{
                showLoadingDialog()
                views.apply {
                    displayName.error = null
                    username.error = null
                    email.error = null
                    password.error = null
                    confrimPassword.error = null
                }
            }
            is Fail ->{
                Log.e("Test Sign", "Fail")
                dismissLoadingDialog()
            }
        }
    }
}