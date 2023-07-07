package com.oceantech.tracking.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.fragment.navArgs
import com.airbnb.mvrx.activityViewModel
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.FragmentUpdateProfileBinding
import com.oceantech.tracking.ui.home.HomeViewModel
import okhttp3.internal.userAgent

class UpdateProfileFragment : TrackingBaseFragment<FragmentUpdateProfileBinding>() {
    private val viewModel:HomeViewModel by activityViewModel()
    lateinit var newFirstName:String
    lateinit var newLastName:String
    lateinit var newGender:String
    lateinit var newBirthday:String
    lateinit var newBirthPlace:String
    lateinit var newUniversity:String
    lateinit var newYear:String

    lateinit var user:User

    private val args:UpdateProfileFragmentArgs by navArgs()
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUpdateProfileBinding = FragmentUpdateProfileBinding.inflate(inflater,container,false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        user = args.user
        setData()
    }

    private fun setData() {
        views.apply {
            firstName.setText(user.firstName.toString())
            lastName.setText(user.lastName.toString())
            birthPlace.setText(user.birthPlace.toString())
            university.setText(user.university.toString())

            send.setOnClickListener {
                send()
            }
        }
    }

    private fun send(){
        newFirstName = views.firstName.text.toString().trim()
        newLastName = views.lastName.text.toString().trim()
        newBirthPlace = views.birthPlace.text.toString().trim()
        newUniversity = views.university.text.toString().trim()

        if((views.genderSpinner.selectedItem as String) == EnMale || (views.genderSpinner.selectedItem as String) == ViMale)
            newGender = EnMale
        else if((views.genderSpinner.selectedItem as String) == EnFemale || (views.genderSpinner.selectedItem as String) == ViFemale)
            newGender = EnFemale
        else if((views.genderSpinner.selectedItem as String) == EnAnother || (views.genderSpinner.selectedItem as String) == ViAnother)
            newGender = EnAnother

        newYear = if((views.yearSpinner.selectedItem as String) == EnGraduated || (views.yearSpinner.selectedItem as String) == ViGraduated)
            "-1"
        else
            views.yearSpinner.selectedItem as String

        if(newFirstName.isNullOrEmpty()) views.firstName.error = requireContext().getString(R.string.username_not_empty)
        if(newLastName.isNullOrEmpty()) views.lastName.error = requireContext().getString(R.string.username_not_empty)
        if(newBirthPlace.isNullOrEmpty()) views.birthPlace.error = requireContext().getString(R.string.username_not_empty)
        if(newUniversity.isNullOrEmpty()) views.university.error = requireContext().getString(R.string.username_not_empty)
        if(!newFirstName.isNullOrEmpty() && !newLastName.isNullOrEmpty() && !newBirthPlace.isNullOrEmpty() && !newUniversity.isNullOrEmpty()){
            user.apply {
                this.firstName = newFirstName
                this.lastName = newLastName
                this.birthPlace = newBirthPlace
                this.university = newUniversity
            }
            viewModel.handleNextUpdateInfo(user)
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