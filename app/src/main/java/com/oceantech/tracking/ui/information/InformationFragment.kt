package com.oceantech.tracking.ui.information

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.FragmentInformationBinding

import com.oceantech.tracking.utils.checkStatusApiRes
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar


class InformationFragment : TrackingBaseFragment<FragmentInformationBinding>() {
    private val args: InformationFragmentArgs by navArgs()
    private lateinit var menu: Menu
    var user: User? = null

    private val infoViewModel: InformationViewModel by activityViewModel()

    // khai báo đối tượng launcher nhận vào input là một intent
    private var myLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        // đăng ký nhận kết quả trả về từ activiy con
        myLauncher = registerForActivityResult(StartActivityForResult(), this::handleResult)

    }

    private fun handleResult(result: ActivityResult) {
        val data = result.data
        val resultCode = result.resultCode
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val fileUri = data?.data!!

            val mProfileUri = fileUri
            views.imageUser.setImageURI(fileUri)
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(requireActivity(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireActivity(), "Task Cancelled", Toast.LENGTH_SHORT).show()
        }

    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentInformationBinding {
        return FragmentInformationBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = args.ReviveUser

        val spinner = views.setting.genderSpinner
        spinner.background = ContextCompat.getDrawable(requireContext(), R.drawable.border)
        val items = arrayOf("Nam", "Nữ", "Unknown")
        val userGender = user?.gender ?: "Unknown"
        val selectedIndex = items.indexOf(userGender)
        spinner.setItems(*items)
        spinner.selectedIndex = selectedIndex
        spinner.setOnItemSelectedListener { view, position, id, item ->
            Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
        }
        views.setting.acc.text = user?.username
        views.setting.username.text =
            user?.displayName?.let { Editable.Factory.getInstance().newEditable(it) }
        views.setting.edtEmail.text =
            user?.email?.let { Editable.Factory.getInstance().newEditable(it) }
        views.setting.edtUniversity.hint = user?.university
        Glide.with(requireContext()).load("user.link").placeholder(R.drawable.ic_person)
            .into(views.imageUser)
        views.setting.spinnerDatePicker.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                showDatePicker()
            }
            true
        }

        views.edtPhoto.setOnClickListener {
            ImagePicker.with(this)
                .compress(1024)         //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    1080,
                    1080
                )  //Final image resolution will be less than 1080 x 1080(Optional)
                .createIntent { intent ->
                    myLauncher?.launch(intent)
                }
        }


        infoViewModel.observeViewEvents {
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireActivity(),
            AlertDialog.THEME_HOLO_LIGHT,
            DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "${selectedDay}/${selectedMonth + 1}/${selectedYear}"
                views.setting.spinnerDatePicker.solidColor
                views.setting.spinnerDatePicker.text = selectedDate
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        this.menu = menu
        inflater.inflate(R.menu.edt_info_menu, menu)
        val editMenuItem = menu.findItem(R.id.action_edit)
        val saveMenuItem = menu.findItem(R.id.action_save)
        // Ban đầu ẩn menu item "Lưu"
        saveMenuItem.isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit -> {
                // Người dùng nhấn "Sửa"
                // Ẩn menu item "Sửa" và hiển thị menu item "Lưu"
                val editMenuItem = menu.findItem(R.id.action_edit)
                val saveMenuItem = menu.findItem(R.id.action_save)
                editMenuItem.isVisible = false
                saveMenuItem.isVisible = true
                views.setting.genderSpinner.isEnabled = true
                views.setting.spinnerDatePicker.isEnabled = true
                views.setting.edtUniversity.isEnabled = true
                views.setting.edtAddress.isEnabled = true
                return true
            }

            R.id.action_save -> {
                val editMenuItem = menu.findItem(R.id.action_edit)
                val saveMenuItem = menu.findItem(R.id.action_save)
                editMenuItem.isVisible = true
                saveMenuItem.isVisible = false
                views.setting.genderSpinner.isEnabled = false
                views.setting.spinnerDatePicker.isEnabled = false
                views.setting.edtAddress.isEnabled = false
                views.setting.edtUniversity.isEnabled = false
                Log.e("user1", user.toString())
                if (user != null) {
                    user!!.displayName = views.setting.username.text.toString().trim()
                    user!!.university = views.setting.edtUniversity.text.toString().trim()
                    user!!.gender = views.setting.genderSpinner.text.toString()
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy")
                    //user!!.dob = dateFormat.parse(views.setting.spinnerDatePicker.text.toString())
                    user!!.birthPlace = views.setting.edtAddress.text.toString().trim()
                    Log.e("user2", user.toString())
                    infoViewModel.handle(InformationViewActon.UpdateUserAction(user!!))
                }


                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun invalidate() = withState(infoViewModel) {
        when (it.user) {
            is Success -> {
                user = it.user.invoke()
                Timber.e("UsersFragment Success: $user")
                Toast.makeText(requireContext(), getString(R.string.success), Toast.LENGTH_SHORT)
                    .show()
            }

            is Fail -> {
                Toast.makeText(
                    requireContext(),
                    getString(checkStatusApiRes(it.user)),
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {}
        }
    }

}