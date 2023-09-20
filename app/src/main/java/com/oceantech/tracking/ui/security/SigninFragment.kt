package com.oceantech.tracking.ui.security

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.DialogDatePickerBinding
import com.oceantech.tracking.databinding.FragmentSigninBinding
import com.oceantech.tracking.utils.*
import com.oceantech.tracking.utils.StringUltis.dateFormat
import com.oceantech.tracking.utils.StringUltis.dateIso8601Format2
import timber.log.Timber
import java.util.*


class SigninFragment : TrackingBaseFragment<FragmentSigninBinding>() {

    private val viewModel: SecurityViewModel by activityViewModel()

    lateinit var firstName: String
    lateinit var lassName: String
    lateinit var displayName: String
    var dob : Date? = null
    lateinit var email: String
    lateinit var userName: String
    lateinit var password: String
    lateinit var gender: String

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSigninBinding {
        return FragmentSigninBinding.inflate(inflater,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinner()

        listenerUIClick()
    }

    private fun setupSpinner() {
        var adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listOf(getString(R.string.male), getString(R.string.female)))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        views.gender.adapter = adapter
    }

    private fun listenerUIClick() {
        views.birthday.setOnClickListener{
            showDateDialog()
        }

        views.send.setOnClickListener{
            val resources = resources
            if (checkTILNull(resources, views.lastName) or checkTILNull(resources, views.firstName)
                or checkTILNull(resources, views.username) or checkValidEmail(resources, views.email)
                or checkTILNull(resources, views.birthday) or checkTILNull(resources, views.password)
                or checkTILNull(resources, views.confirmPassword) or checkValidEPassword(resources, views.password, views.confirmPassword)
            )
            else signinSubmit()
        }


    }

    private fun showDateDialog() {
        val dialog = Dialog(requireContext())
        val dialogBinding = DialogDatePickerBinding.inflate(requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
        dialog.setContentView(dialogBinding.root)

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        dialog.show()
        dialog.window?.apply {
            this.attributes = layoutParams
            this.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }


        var calendar: Calendar = Calendar.getInstance()
        if (dob != null) calendar.time = dob!!
        var dateTempt: Date = calendar.time

        dialogBinding.datePickerDob.init(calendar.get(Calendar.YEAR), Calendar.MONTH, Calendar.DAY_OF_MONTH
        ) { view, year, monthOfYear, dayOfMonth ->
            dateTempt = Date(year - 1900, monthOfYear, dayOfMonth)
        }

        dialogBinding.done.setOnClickListener{
            dob = dateTempt
            views.birthday.setText(dateFormat.format(dob!!))
            dialog.dismiss()
        }

        dialogBinding.cancel.setOnClickListener{
            dialog.dismiss()
        }
    }

    private fun signinSubmit() {
        firstName = views.firstName.text.toString().trim()
        lassName = views.lastName.text.toString().trim()
        displayName = "$lassName $firstName"
        email = views.email.text.toString().trim()
        userName = views.username.text.toString().trim()
        password = views.password.text.toString().trim()

        if (views.gender.selectedItemPosition == 0) gender = "M"
        else if (views.gender.selectedItemPosition == 1) gender = "L"

        var usser2 = User(null,
            userName,
            true,
            null,
            true,
            password,
            displayName,
            dob?.convertDateToStringFormat(dateIso8601Format2),
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
            null,
            0 )

        Timber.e(usser2.toString())
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

    override fun onDestroy() {
        super.onDestroy()
        viewModel.handle(SecurityViewAction.RemoveUserCurrent)
    }
}