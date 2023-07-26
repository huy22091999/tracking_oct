package com.oceantech.tracking.ui.profile

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.fragment.navArgs
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.FragmentUpdateProfileBinding
import com.oceantech.tracking.ui.home.HomeViewEvent
import com.oceantech.tracking.ui.home.HomeViewModel
import com.oceantech.tracking.utils.initialAlertDialog
import okhttp3.internal.userAgent
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UpdateProfileFragment : TrackingBaseFragment<FragmentUpdateProfileBinding>() {
    private val viewModel:HomeViewModel by activityViewModel()
    lateinit var newFirstName:String
    lateinit var newLastName:String
    private var newGender:String = EnMale
    lateinit var newBirthday:String
    lateinit var newBirthPlace:String
    lateinit var newUniversity:String
    private var newYear:String = "1"

    var isUpdateMyself:Boolean = false

    lateinit var user:User

    private val args:UpdateProfileFragmentArgs by navArgs()
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUpdateProfileBinding = FragmentUpdateProfileBinding.inflate(inflater,container,false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeViewEvents {
            handleEvents(it)
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

        user = args.user
        isUpdateMyself = args.isMyself
        setData()

        views.backLayout.setOnClickListener {
            if(isUpdateMyself){
                viewModel.handleReturnProfile()
            }
            else viewModel.handleReturnUsers()
        }

        views.calendarImage.setOnClickListener {
            showDatePickerDialog()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("user", user)
    }
    private fun handleEvents(it: HomeViewEvent) {
        when(it){
            is HomeViewEvent.ResetLanguege -> {
                setData()
            }
            is HomeViewEvent.ResetTheme -> {
                setData()
            }
        }
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
            viewModel.handleNextUpdateInfo(user,isUpdateMyself)
            Log.i("check data: ", user.toString())
            views.apply {
                firstName.error = null
                lastName.error = null
                university.error = null
                birthPlace.error = null
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                newBirthday = dateFormat.format(selectedDate.time).toString()
                views.labelBirthday.text = newBirthday
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
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