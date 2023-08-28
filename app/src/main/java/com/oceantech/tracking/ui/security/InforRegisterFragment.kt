package com.oceantech.tracking.ui.security

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.viewModel
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.FragmentInforRegisterBinding

class InforRegisterFragment : TrackingBaseFragment<FragmentInforRegisterBinding>() {
    private val viewModel:SecurityViewModel by activityViewModel()

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentInforRegisterBinding {
        return FragmentInforRegisterBinding.inflate(inflater,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        views.next.setOnClickListener {
            val firstName = views.firstName.text.toString()
            val lastName = views.lastName.text.toString()
            val gender = onRadioButtonClicked()
            val birthPlace = views.birthPlace.text.toString()
            val university = views.university.text.toString()
            val year = views.txtYear.text.toString()

            if (checkEmpty(lastName, firstName, birthPlace, university, year)) {
                setErrorsIfEmpty(lastName, firstName, birthPlace, university, year)
            } else if (!isNumeric(year)) {
                views.txtYearTil.error=getString(R.string.year_is_not_string)
            } else {
                // Tiến hành xử lý dữ liệu khi các trường đều hợp lệ
                val user = User(
                    firstName = firstName,
                    lastName = lastName,
                    gender = gender,
                    birthPlace = birthPlace,
                    university = university,
                    year = year.toInt(),
                    active = true,
                    changePass = false,
                    countDayCheckin = 0,
                    countDayTracking = 0,
                    dob = "",
                    hasPhoto = false,
                    id = null,
                    roles = listOf(),
                    setPassword = true,
                    tokenDevice = "",
                    password = "",
                    confirmPassword = "",
                    displayName = "",
                    username = "",
                    email = ""
                )
                viewModel.handleReturnSignin(user)
            }
        }

    }
    private fun isNumeric(input: String): Boolean {
        return input.toIntOrNull() != null
    }

    private fun setErrorsIfEmpty(lastName:String,firstName:String,birthPlace:String,university:String,year:String){
        if(lastName.isNullOrEmpty()) views.lastNameTil.error=getString(R.string.username_not_empty)
        if(firstName.isNullOrEmpty()) views.firstNameTil.error=getString(R.string.username_not_empty)
        if(birthPlace.isNullOrEmpty()) views.birthPlace.error=getString(R.string.username_not_empty)
        if(university.isNullOrEmpty()) views.universityTil.error=getString(R.string.username_not_empty)
        if(year.isNullOrEmpty()) views.txtYearTil.error=getString(R.string.username_not_empty)

    }

    private fun checkEmpty(lastName:String,firstName:String,birthPlace:String,university:String,year:String):Boolean{
        return firstName.isEmpty() ||
                lastName.isEmpty() ||
                birthPlace.isEmpty() ||
                university.isEmpty() ||
                year.isEmpty()
    }

    // Function to get the selected gender from the RadioGroup
    private fun onRadioButtonClicked(): String {
        val radioGroup = views.gender
        val selectedRadioButtonId = radioGroup.checkedRadioButtonId
        val selectedRadioButton = views.root.findViewById<RadioButton>(selectedRadioButtonId)
        return selectedRadioButton.text.toString()
    }


}