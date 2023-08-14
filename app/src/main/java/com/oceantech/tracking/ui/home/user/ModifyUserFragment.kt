package com.oceantech.tracking.ui.home.user

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.databinding.FragmentModifyUserBinding
import com.oceantech.tracking.ui.home.HomeViewAction
import com.oceantech.tracking.ui.home.HomeViewModel
import com.oceantech.tracking.ui.home.HomeViewState
import com.oceantech.tracking.utils.NavigationFragment
import com.oceantech.tracking.utils.checkEmpty
import com.oceantech.tracking.utils.checkError
import com.oceantech.tracking.utils.emptyOrText
import com.oceantech.tracking.utils.handleBackPressedEvent
import com.oceantech.tracking.utils.selectDateAndCheckError
import com.oceantech.tracking.utils.setUpDropdownMenu
import com.oceantech.tracking.utils.showToast
import com.oceantech.tracking.utils.toIsoInstant
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ModifyUserFragment @Inject constructor() : TrackingBaseFragment<FragmentModifyUserBinding>(),
    NavigationFragment {

    private val homeViewModel: HomeViewModel by activityViewModel()

    companion object {
        private const val GET_USER_ID = 1
        private const val UPDATE_ID_USER = 2
    }

    private var stateModify: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            homeViewModel.handle(HomeViewAction.GetUser(it.getInt(UserInfoFragment.UPDATE_ID, 0)))
            stateModify = GET_USER_ID
        }
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentModifyUserBinding = FragmentModifyUserBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleBackPressedEvent(findNavController()) {
            updateAndCheck()
        }
        views.editDob.selectDateAndCheckError(
            requireContext(),
            getString(R.string.dob_error_signin)
        )

        setUpDropdownMenu(
            views.editGender,
            resources.getStringArray(R.array.list_gender).asList(),
            requireContext(),
            views.userGenderLayout,
            getString(R.string.gender_error_signin)
        )
        setUpDropdownMenu(
            views.editStudentYear,
            resources.getStringArray(R.array.list_year).asList(),
            requireContext(),
            views.userYearLayout,
            getString(R.string.year_error_signin)
        )

        checkEmpty()
        views.updateInfo.setOnClickListener {
            updateAndCheck()
        }
    }

    override fun invalidate(): Unit = withState(homeViewModel) {
        when (stateModify) {
            GET_USER_ID -> handleGetUser(it)
            UPDATE_ID_USER -> handleUpdateUser(it)
        }
    }

    private fun handleUpdateUser(state: HomeViewState) {
        when (val update = state.updateUser) {
            is Success -> {
                showToast(requireContext(), getString(R.string.update_user_successfully))
                val bundle = bundleOf(UserInfoFragment.UPDATE_ID to update.invoke().id)
                findNavController().navigate(R.id.userInfoFragment, bundle)
            }

            is Fail -> {
                update.error.message?.let { checkError(it) }
            }

            else -> {}
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleGetUser(state: HomeViewState) {
        when (val user = state.getUser) {
            is Success -> {
                user.invoke().let {
                    views.user = it
                    views.editStudentYear.setText(if (it.year == 5) getString(R.string.graduated) else it.year.toString())
                }

            }

            is Fail -> {
                user.error.message?.let { checkError(it) }
            }

            else -> {}
        }
    }

    private fun updateUser(
        displayName: String,
        dob: String,
        gender: String,
        email: String,
        university: String,
        year: String
    ) {
        val updateUser = views.user?.copy(
            displayName = displayName,
            dob = toIsoInstant(dob),
            gender = gender,
            email = email,
            university = university,
            year = year.toInt()
        )
        updateUser?.id?.let { id ->
            homeViewModel.handle(HomeViewAction.UpdateUser(updateUser, id))
            stateModify = UPDATE_ID_USER
        }
    }

    private fun updateAndCheck() {
        val displayName =
            views.editDisplayName.emptyOrText(getString(R.string.displayname_error_signin))
        val dob = views.editDob.emptyOrText(getString(R.string.dob_error_signin))
        val gender = views.editGender.emptyOrText(getString(R.string.gender_error_signin))
        val email = views.editEmail.emptyOrText(getString(R.string.email_error_signin))
        val university = views.editUniversity.emptyOrText(getString(R.string.uni_error_signin))
        val year = views.editStudentYear.emptyOrText(getString(R.string.year_error_signin))

        if (displayName.isNotEmpty() && gender.isNotEmpty()
            && dob.isNotEmpty() && email.isNotEmpty() && university.isNotEmpty() && year.isNotEmpty()
        ) {
            updateUser(displayName, dob, gender, email, university, year)
        }
    }

    private fun checkEmpty() {
        views.editDisplayName.checkEmpty(getString(R.string.displayname_error_signin))
        views.editEmail.checkEmpty(getString(R.string.email_error_signin))
        views.editUniversity.checkEmpty(getString(R.string.uni_error_signin))
    }

    override fun handleAction() {
        updateAndCheck()
    }

}