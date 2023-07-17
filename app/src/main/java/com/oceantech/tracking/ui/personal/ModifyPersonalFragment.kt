package com.oceantech.tracking.ui.personal

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.oceantech.tracking.utils.checkError
import com.oceantech.tracking.utils.handleBackPressedEvent
import com.oceantech.tracking.utils.showToast


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
        handleBackPressedEvent(findNavController()){
            updateMyself()
        }
        views.updateInfo.setOnClickListener {
            updateMyself()
        }

    }

    private fun updateMyself() {
        val updateMyself = views.user?.copy(
            firstName = views.editFirstName.text.toString(),
            lastName = views.editLastName.text.toString(),
            displayName = views.editDisplayName.text.toString(),
            dob = views.editDob.text.toString(),
            gender = views.editGender.text.toString(),
            email = views.editEmail.text.toString(),
            university = views.editUniversity.text.toString(),
            year = views.editStudentYear.text.toString().toInt()
        )
        updateMyself?.let {myself ->
            viewModel.handle(HomeViewAction.UpdateMyself(myself))
            modifyState = MODIFY_MYSELF
        }
    }

    override fun invalidate(): Unit = withState(viewModel){
        when(modifyState) {
            GET_MYSELF -> handleGetMyself(it)
            MODIFY_MYSELF -> handleModifyMyself(it)
        }
    }

    private fun handleGetMyself(state: HomeViewState) {
        when(val myself = state.userCurrent){
            is Success -> {
                views.user = myself.invoke()
            }
            is Fail -> {
                myself.error.message?.let { checkError(it) }
            }
            else -> {}
        }
    }

    private fun handleModifyMyself(state: HomeViewState) {
        when(val modifyMyself = state.updateMyself) {
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
}