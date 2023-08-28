package com.oceantech.tracking.ui.security

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.CalendarView.OnDateChangeListener
import android.widget.DatePicker
import android.widget.Toast
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.FragmentSigninBinding
import com.oceantech.tracking.ui.MainActivity
import com.oceantech.tracking.utils.checkRadioGRNull
import com.oceantech.tracking.utils.checkStatusApiRes
import com.oceantech.tracking.utils.checkTILNull
import com.oceantech.tracking.utils.checkValidEmail
import java.util.Calendar
import java.util.Date


class SigninFragment : TrackingBaseFragment<FragmentSigninBinding>() {

    private val viewModel: SecurityViewModel by activityViewModel()

    lateinit var firstName: String
    lateinit var lassName: String
    lateinit var displayName: String
    var dob : Date? = null
    lateinit var email: String
    lateinit var userName: String
    lateinit var password: String
    var gender: String? = null

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSigninBinding {
        return FragmentSigninBinding.inflate(inflater,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listenerUIClick()
    }

    private fun listenerUIClick() {
        views.datePickerDob.init(2000, 1, 1
        ) { view, year, monthOfYear, dayOfMonth ->
            dob = Date(year, monthOfYear, dayOfMonth)
        }

        views.send.setOnClickListener{
            val resources = resources
            if (checkTILNull(resources, views.lastName) or checkTILNull(resources, views.firstName)
                or checkTILNull(resources, views.username) or checkTILNull(resources, views.password)
                or checkRadioGRNull(views.radioGroup) or checkValidEmail(resources, views.email)
            )
            else signinSubmit()
        }
    }

    private fun signinSubmit() {
        firstName = views.firstName.text.toString().trim()
        lassName = views.lastName.text.toString().trim()
        displayName = "$lassName $firstName"
        email = views.email.text.toString().trim()
        userName = views.username.text.toString().trim()
        password = views.password.text.toString().trim()

        if (views.radioMale.isChecked) gender = "male"
        else if (views.radioFimale.isChecked) gender = "female"

        var usser2 = User(null,
            userName,
            true,
            null,
            true,
            password,
            displayName,
            dob,
            email,
            firstName,
            lassName,
            password,
            true,
            listOf(),
            0,
            0 ,
            gender,
        false,
            "",
            "",
            0 )
        viewModel.handle(SecurityViewAction.SigninAction(usser2))
    }

    override fun invalidate(): Unit  = withState(viewModel){
        when(it.userCurrent){
            is Success ->{
                Toast.makeText(requireContext(),getString(R.string.signin_success), Toast.LENGTH_LONG).show()
                activity?.supportFragmentManager?.popBackStack()
            }
            is Fail -> {
                Toast.makeText(requireContext(), getString(checkStatusApiRes(it.userCurrent as Fail<User>)), Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }
}