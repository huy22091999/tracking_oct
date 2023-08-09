package com.oceantech.tracking.ui.personal

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.databinding.FragmentModifyPersonalBinding
import com.oceantech.tracking.ui.home.HomeViewAction
import com.oceantech.tracking.ui.home.HomeViewModel
import com.oceantech.tracking.ui.home.HomeViewState
import com.oceantech.tracking.ui.home.user.ModifyUserFragment
import com.oceantech.tracking.utils.checkEmpty
import com.oceantech.tracking.utils.checkError
import com.oceantech.tracking.utils.emptyOrText
import com.oceantech.tracking.utils.handleBackPressedEvent
import com.oceantech.tracking.utils.hideKeyboard
import com.oceantech.tracking.utils.selectDateAndCheckError
import com.oceantech.tracking.utils.setUpDropdownMenu
import com.oceantech.tracking.utils.showToast
import com.oceantech.tracking.utils.toIsoInstant
import java.util.Calendar


class ModifyPersonalFragment : TrackingBaseFragment<FragmentModifyPersonalBinding>() {

    private val viewModel: HomeViewModel by activityViewModel()
    private var modifyState: Int = 0

    companion object {
        private const val GET_MYSELF = 1
        private const val MODIFY_MYSELF = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.handle(HomeViewAction.GetCurrentUser)
        modifyState = GET_MYSELF

    }


    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentModifyPersonalBinding =
        FragmentModifyPersonalBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleBackPressedEvent(findNavController()) {
            checkAndUpdate()
        }
        views.editDob.selectDateAndCheckError(
            requireContext(),
            getString(R.string.dob_error_signin)
        )

        setUpDropdownMenu(
            views.editGender,
            resources.getStringArray(R.array.list_gender).asList(),
            requireContext(),
            views.editGenderLayout,
            getString(R.string.gender_error_signin)
        )
        setUpDropdownMenu(
            views.editStudentYear,
            resources.getStringArray(R.array.list_year).asList(),
            requireContext(),
            views.layoutYear,
            getString(R.string.year_error_signin)
        )

        checkEmpty()

        views.updateInfo.setOnClickListener {
            checkAndUpdate()
        }

    }

    private fun updateMyself() {
        val updateMyself = views.user?.copy(
            displayName = views.editDisplayName.text.toString(),
            dob = toIsoInstant(views.editDob.text.toString()),
            gender = views.editGender.text.toString(),
            email = views.editEmail.text.toString(),
            university = views.editUniversity.text.toString(),
            year = views.editStudentYear.let {
                var text = it.text.toString()
                if (text == resources.getStringArray(R.array.list_year).last()) {
                    text = "5"
                }
                text.toInt()
            }
        )
        updateMyself?.let { myself ->
            viewModel.handle(HomeViewAction.UpdateMyself(myself))
            modifyState = MODIFY_MYSELF
        }
    }

    override fun invalidate(): Unit = withState(viewModel) {
        when (modifyState) {
            GET_MYSELF -> handleGetMyself(it)
            MODIFY_MYSELF -> handleModifyMyself(it)
        }
    }

    private fun handleGetMyself(state: HomeViewState) {
        when (val myself = state.userCurrent) {
            is Success -> {
                myself.invoke().let {
                    views.user = it
                    views.editStudentYear.setText(if (it.year == 5) getString(R.string.graduated) else it.year.toString())
                }

            }

            is Fail -> {
                myself.error.message?.let { checkError(it) }
            }

            else -> {}
        }
    }

    private fun handleModifyMyself(state: HomeViewState) {
        when (val modifyMyself = state.updateMyself) {
            is Success -> {
                findNavController().navigate(R.id.personalFragment)
                showToast(requireContext(), getString(R.string.update_user_successfully))
            }

            is Fail -> {
                modifyMyself.error.message?.let { checkError(it) }
            }

            else -> {}
        }
    }


    // Check empty or not when user untouch view
    private fun checkEmpty() {
        views.editDisplayName.checkEmpty(getString(R.string.displayname_error_signin))
        views.editEmail.checkEmpty(getString(R.string.email_error_signin))
        views.editUniversity.checkEmpty(getString(R.string.uni_error_signin))
    }

    private fun checkAndUpdate() {
        val displayName =
            views.editDisplayName.emptyOrText(getString(R.string.displayname_error_signin))
        val gender = views.editGender.emptyOrText(getString(R.string.gender_error_signin))
        val dob = views.editDob.emptyOrText(getString(R.string.dob_error_signin))
        val email = views.editEmail.emptyOrText(getString(R.string.email_error_signin))
        val university = views.editUniversity.emptyOrText(getString(R.string.uni_error_signin))
        val year = views.editStudentYear.emptyOrText(getString(R.string.year_error_signin))

        if (displayName.isNotEmpty() && gender.isNotEmpty()
            && dob.isNotEmpty() && email.isNotEmpty() && university.isNotEmpty() && year.isNotEmpty()
        ) {
            updateMyself()
        }
    }
}