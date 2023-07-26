package com.oceantech.tracking.ui.security

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.databinding.FragmentSigninBinding
import com.oceantech.tracking.utils.checkEmpty
import com.oceantech.tracking.utils.hideKeyboard
import com.oceantech.tracking.utils.emptyOrText
import com.oceantech.tracking.utils.registerNetworkReceiver
import com.oceantech.tracking.utils.toIsoInstant
import com.oceantech.tracking.utils.unregisterNetworkReceiver
import timber.log.Timber
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
        views.dob.apply {
            inputType = EditorInfo.TYPE_NULL
            onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                v.hideKeyboard()
                if (hasFocus) {
                    // Use date picker to choose date
                    chooseDate()
                    v.setOnClickListener {
                        chooseDate()
                    }
                } else {
                    if (views.dob.text.isNullOrBlank()) {
                        views.layoutDob.error = getString(R.string.dob_error_signin)
                    }
                }
            }
        }
        val adapter = ArrayAdapter(
            requireContext(),
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            resources.getStringArray(R.array.list_gender)
        )
        views.gender.apply {
            setAdapter(adapter)
            setOnItemClickListener { _, _, position, _ ->
                setText(adapter.getItem(position))
            }
            setOnFocusChangeListener { _, hasFocus ->
                hideKeyboard()
                if (hasFocus) {
                    showDropDown()
                    setOnClickListener {
                        showDropDown()
                    }
                } else {
                    if (views.gender.text.isNullOrBlank()) {
                        views.genderLayout.error = getString(R.string.gender_error_signin)
                    }
                }
            }
        }

        checkEmpty()

        views.send.setOnClickListener {
            val userName = views.username.emptyOrText(getString(R.string.username_error_signin))
            val password = views.password.emptyOrText(getString(R.string.password_error_signin))
            val displayName = views.displayName.emptyOrText(getString(R.string.displayname_error_signin))
            val gender = views.gender.emptyOrText(getString(R.string.gender_error_signin))
            val dob = views.dob.emptyOrText(getString(R.string.dob_error_signin))
            val email = views.email.emptyOrText(getString(R.string.email_error_signin))
            val university = views.university.emptyOrText(getString(R.string.uni_error_signin))
            val year = views.studentYear.emptyOrText(getString(R.string.year_error_signin))
            val confirmPassword = views.rePassword.emptyOrText(getString(R.string.confirmpass_error_signin))

            if (userName.isNotEmpty() && password.isNotEmpty() && displayName.isNotEmpty() && gender.isNotEmpty()
                && dob.isNotEmpty() && email.isNotEmpty() && university.isNotEmpty() && year.isNotEmpty() && confirmPassword.isNotEmpty()
            ) {
                if (confirmPassword != password) {
                    views.confirmPassLayout.error = getString(R.string.confirmpass_same_pass_error)
                } else {
                    viewModel.handle(
                        SecurityViewAction.SignInAction(
                            userName,
                            password,
                            displayName,
                            gender,
                            toIsoInstant(dob),
                            email, university,
                            year.toInt(),
                            confirmPassword
                        )
                    )
                    stateSignIn = SIGN_IN
                }

            }
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
                Toast.makeText(requireContext(), getString(R.string.register_successfully), Toast.LENGTH_SHORT).show()
                parentFragmentManager.commit {
                    add<LoginFragment>(R.id.frame_layout)
                }
                stateSignIn = 0
            }

            is Fail -> {
                Timber.tag("SignIn").i(state.userSignIn.error.toString())
                Toast.makeText(
                    requireContext(),
                    getString(R.string.existed_username_error),
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

    private fun chooseDate() {
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

    // Check empty or not when user unclick view
    private fun checkEmpty() {
        views.username.checkEmpty(getString(R.string.username_error_signin))
        views.password.checkEmpty(getString(R.string.password_error_signin))
        views.displayName.checkEmpty(getString(R.string.displayname_error_signin))
        views.email.checkEmpty(getString(R.string.email_error_signin))
        views.university.checkEmpty(getString(R.string.uni_error_signin))
        views.studentYear.checkEmpty(getString(R.string.year_error_signin))
        views.rePassword.checkEmpty(getString(R.string.confirmpass_error_signin))
    }
}