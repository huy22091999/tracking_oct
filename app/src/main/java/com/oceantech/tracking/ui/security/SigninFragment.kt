package com.oceantech.tracking.ui.security

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.databinding.FragmentSigninBinding
import com.oceantech.tracking.utils.checkError
import com.oceantech.tracking.utils.registerNetworkReceiver
import com.oceantech.tracking.utils.toIsoInstant
import com.oceantech.tracking.utils.unregisterNetworkReceiver
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)

class SigninFragment : TrackingBaseFragment<FragmentSigninBinding>() {

    private val viewModel: SecurityViewModel by activityViewModel()

    private var stateSignIn: Int = 0

    companion object {
        private const val SIGN_IN = 1
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSigninBinding {
        return FragmentSigninBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerNetworkReceiver { }
        views.layoutDob.setStartIconOnClickListener {
            //Hide keyboard
            val inputMethod =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethod.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)

            // Use date picker to choose date
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            DatePickerDialog(
                requireContext(),
                { view, year, month, dayOfMonth ->
                    val date = String.format("%02d-%02d-%04d", dayOfMonth, month + 1, year)
                    views.dob.setText(date)
                },
                year,
                month,
                dayOfMonth
            ).show()
        }
        views.send.setOnClickListener {
            val userName = views.username.text.toString()
            val password = views.password.text.toString()
            val displayName = views.displayName.text.toString()
            val firstName = views.firstName.text.toString()
            val lastName = views.lastName.text.toString()
            val gender = views.gender.text.toString()
            val dob = toIsoInstant(views.dob.text.toString().also {
                if(it.contains("/")){
                    it.replace("/", "-")
                }
            })
            val email = views.email.text.toString()
            val university = views.university.text.toString()
            val year = views.studentYear.text.toString().toInt()
            val confirmPassword = views.rePassword.text.toString()


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
            stateSignIn = SIGN_IN

        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun invalidate(): Unit = withState(viewModel) {
        when (stateSignIn) {
            SIGN_IN -> handleSignIn(it)
            else -> {}
        }
    }

    private fun handleSignIn(state: SecurityViewState) {
        when (state.userSignIn) {
            is Success -> {
                Toast.makeText(requireContext(), "Register Successfully", Toast.LENGTH_SHORT).show()
                parentFragmentManager.commit {
                    add<LoginFragment>(R.id.frame_layout)
                }
                stateSignIn = 0
            }

            is Fail -> {
                Log.i("SignIn", state.userSignIn.error.toString())
                Toast.makeText(
                    requireContext(),
                    "Username has been already existed ",
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {}
        }
    }

    override fun onDestroy() {
        unregisterNetworkReceiver()
        super.onDestroy()
    }
}