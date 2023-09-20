package com.oceantech.tracking.ui.profile.edit

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.view.isVisible
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.DialogDatePickerBinding
import com.oceantech.tracking.databinding.FragmentEditProfileBinding
import com.oceantech.tracking.ui.profile.InfoActivity
import com.oceantech.tracking.ui.profile.InfoViewModel
import com.oceantech.tracking.ui.profile.InfoViewsAction
import com.oceantech.tracking.ui.security.LoginActivity
import com.oceantech.tracking.utils.*
import com.oceantech.tracking.utils.StringUltis.dateIso8601Format
import com.oceantech.tracking.utils.StringUltis.dateIso8601Format2
import java.util.*


class EditProfileFragment : TrackingBaseFragment<FragmentEditProfileBinding>() {

    private val infoViewModel: InfoViewModel by activityViewModel()
    private lateinit var textViewToolbar: TextView
    var dob : Date? = null
    var user: User? = null

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEditProfileBinding {
        return FragmentEditProfileBinding.inflate(layoutInflater)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolBar()
        setupSpinner()
        lisenClickUI()
    }

    private fun setUpToolBar() {
        textViewToolbar = (requireActivity() as InfoActivity).bindingActivity.tvToolbarRight
        textViewToolbar.isVisible = true
    }

    override fun invalidate() = withState(infoViewModel){
        when(it.userEdit){
            is Success ->{
                user = it.userEdit.invoke()
                views.tilEmail.setText(user?.email)
                views.tilUniversity.setText(user?.university)
                views.tilLever.setText("${user?.year ?: 0}")
                views.tilBirthday.setText(user?.dob?.convertToStringFormat(dateIso8601Format, StringUltis.dateFormat))
                views.tilBirtplace.setText(user?.birthPlace)

                if (user?.gender.equals("M")) views.gender.setSelection(0)
                else if (user?.gender.equals("L")) views.gender.setSelection(1)
            }
        }

        when(it.updateUser){
            is Success ->{
                showSnackbar(views.root, getString(R.string.success), null, R.color.text_title1){}
                infoViewModel.RetunBackToFrg()
            }

            is Fail ->{
                showSnackbar(views.root, getString(R.string.failed), null, R.color.red){}
            }

            else ->{

            }
        }
    }

    private fun lisenClickUI() {

        views.tilBirthday.setOnClickListener{
            showDateDialog()
        }

        textViewToolbar.setOnClickListener{
            if (checkValidEmail(resources, views.tilEmail))
                else updateInfoUser()
        }
    }

    private fun updateInfoUser() {
        if (user == null) return
        user!!.email = views.tilEmail.text.toString()
        user!!.university = views.tilUniversity.text.toString()
        user!!.year = views.tilLever.text.toString().toInt()
        user!!.gender =   if (views.gender.selectedItemPosition == 0) "M" else "L"
        user!!.dob = dob?.convertDateToStringFormat(dateIso8601Format2)
        user!!.birthPlace = views.tilBirtplace.text.toString()

        infoViewModel.handle(InfoViewsAction.UpdateUserAction(user!!))
    }

    private fun setupSpinner() {
        var adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listOf(getString(R.string.male), getString(R.string.female)))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        views.gender.adapter = adapter
    }

    private fun showDateDialog() {
        val dialog = Dialog(requireContext())
        val dialogBinding = DialogDatePickerBinding.inflate(requireContext().getSystemService(
            Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
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
            views.tilBirthday.setText(StringUltis.dateFormat.format(dob!!))
            dialog.dismiss()
        }

        dialogBinding.cancel.setOnClickListener{
            dialog.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        textViewToolbar.isVisible = false
        infoViewModel.handle(InfoViewsAction.RemoveUpdateUserAction)
    }
}