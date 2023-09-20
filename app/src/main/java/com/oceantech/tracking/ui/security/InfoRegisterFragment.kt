package com.oceantech.tracking.ui.security

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.airbnb.mvrx.activityViewModel
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.FragmentInforRegisterBinding
import com.oceantech.tracking.utils.isNumeric
//done
class InfoRegisterFragment : TrackingBaseFragment<FragmentInforRegisterBinding>() {

    private val viewModel:SecurityViewModel by activityViewModel()

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentInforRegisterBinding {
        return FragmentInforRegisterBinding.inflate(inflater,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenEvent()
    }

    private fun listenEvent() {
        views.btnBack.setOnClickListener{
            requireActivity().supportFragmentManager.popBackStack()
        }
        views.next.setOnClickListener {
            onSubmitForm()
        }
    }

    private fun onSubmitForm() {
        val firstName = views.firstName.text.toString()
        val lastName = views.lastName.text.toString()
        val gender = onRadioButtonClicked()
        val birthPlace = views.birthPlace.text.toString()
        val university = views.university.text.toString()
        val year = views.txtYear.text.toString()

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

        if (checkEmpty(user)) {
            setErrorsIfEmpty(user)
        } else if (!isNumeric(year)) {
            views.txtYearTil.error=getString(R.string.year_is_not_string)
        } else {
            viewModel.handleReturnSignUp(user)
        }
    }

    private fun setErrorsIfEmpty(user: User){
        if(user.lastName.isNullOrEmpty()) views.lastNameTil.error=getString(R.string.username_not_empty)
        if(user.firstName.isNullOrEmpty()) views.firstNameTil.error=getString(R.string.username_not_empty)
        if(user.birthPlace.isNullOrEmpty()) views.birthPlace.error=getString(R.string.username_not_empty)
        if(user.university.isNullOrEmpty()) views.universityTil.error=getString(R.string.username_not_empty)
        if(user.year.toString().isNullOrEmpty()) views.txtYearTil.error=getString(R.string.username_not_empty)

    }

    private fun checkEmpty(user: User):Boolean{
        return user.firstName.isEmpty() ||
                user.lastName.isEmpty() ||
                user.birthPlace.isEmpty() ||
                user.university.isEmpty() ||
                user.year.toString().isEmpty()
    }

    private fun onRadioButtonClicked(): String {
        val radioGroup = views.gender
        val selectedRadioButtonId = radioGroup.checkedRadioButtonId
        val selectedRadioButton = views.root.findViewById<RadioButton>(selectedRadioButtonId)
        return selectedRadioButton.text.toString()
    }


}