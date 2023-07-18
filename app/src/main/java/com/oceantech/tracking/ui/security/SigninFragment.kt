package com.oceantech.tracking.ui.security

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.viewbinding.ViewBinding
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.databinding.DialogLoginBinding
import com.oceantech.tracking.databinding.FragmentSigninBinding
import com.oceantech.tracking.ui.MainActivity
import com.oceantech.tracking.utils.validateEmail
import javax.inject.Inject


class SigninFragment @Inject constructor() : TrackingBaseFragment<FragmentSigninBinding>() {
    val viewModel: SecurityViewModel by activityViewModel()
    //lateinit var user:User
    lateinit var firstName:String
    lateinit var lastName:String
    lateinit var gender:String
    lateinit var birthday:String
    lateinit var birthPlace:String
    lateinit var university:String
    lateinit var year:String
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSigninBinding {
        return FragmentSigninBinding.inflate(inflater,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        views.send.setOnClickListener {
            send()
        }
        views.labelSignup.setOnClickListener {
            viewModel.handleReturnLogin()
        }

        val genderAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item,resources.getStringArray(R.array.gender_spin))
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        views.genderSpinner.apply {
            adapter = genderAdapter
        }

        val yearAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item,resources.getStringArray(R.array.year_spinner))
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        views.yearSpinner.apply {
            adapter = yearAdapter
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun send(){
        firstName = views.firstName.text.toString().trim()
        lastName = views.lastName.text.toString().trim()
        birthPlace = views.birthPlace.text.toString().trim()
        university = views.university.text.toString().trim()

        if((views.genderSpinner.selectedItem as String) == EnMale || (views.genderSpinner.selectedItem as String) == ViMale)
            gender = EnMale
        else if((views.genderSpinner.selectedItem as String) == EnFemale || (views.genderSpinner.selectedItem as String) == ViFemale)
            gender = EnFemale
        else if((views.genderSpinner.selectedItem as String) == EnAnother || (views.genderSpinner.selectedItem as String) == ViAnother)
            gender = EnAnother

        if((views.yearSpinner.selectedItem as String) == EnGraduated || (views.yearSpinner.selectedItem as String) == ViGraduated)
            year = "-1"
        else
            year = views.yearSpinner.selectedItem as String

        if(firstName.isNullOrEmpty()) views.firstName.error = requireContext().getString(R.string.username_not_empty)
        if(lastName.isNullOrEmpty()) views.lastName.error = requireContext().getString(R.string.username_not_empty)
        if(birthPlace.isNullOrEmpty()) views.birthPlace.error = requireContext().getString(R.string.username_not_empty)
        if(university.isNullOrEmpty()) views.university.error = requireContext().getString(R.string.username_not_empty)
        if(!firstName.isNullOrEmpty() && !lastName.isNullOrEmpty() && !birthPlace.isNullOrEmpty() && !university.isNullOrEmpty()){
            val user = User(null,
                null,
                true,
                birthPlace,
                false,
                null,
                0,
                0,
                null,
                null,
                null,
                firstName,
                gender,
                true,
                lastName,
                null,
                null,
                listOf(),
                null,
                university,
                year.toInt()
            )
            viewModel.handleReturnNextSignIn(user)
            views.apply {
                firstName.error = null
                lastName.error = null
                university.error = null
                birthPlace.error = null
            }
        }
    }

    companion object {
        private const val ViMale = "Nam"
        private const val ViFemale = "Nữ"
        private const val ViAnother = "Khác"
        private const val ViGraduated = "Đã tốt nghiệp"
        private const val EnMale = "Male"
        private const val EnFemale = "Female"
        private const val EnAnother = "Another"
        private const val EnGraduated = "Graduated"
    }

}