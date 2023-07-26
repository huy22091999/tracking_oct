package com.oceantech.tracking.ui.home.user

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
import com.oceantech.tracking.utils.checkError
import com.oceantech.tracking.utils.handleBackPressedEvent
import com.oceantech.tracking.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ModifyUserFragment @Inject constructor(): TrackingBaseFragment<FragmentModifyUserBinding>() {

    private val homeViewModel: HomeViewModel by activityViewModel()

    companion object{
        private const val GET_BLOCK_ID = 1
        private const val UPDATE_ID_USER = 2
    }

    private var stateModify: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let{
            homeViewModel.handle(HomeViewAction.LockUser(it.getInt(UserInfoFragment.UPDATE_ID, 0)))
            stateModify = GET_BLOCK_ID
        }
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentModifyUserBinding = FragmentModifyUserBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleBackPressedEvent(findNavController()){
            updateUser()
        }
        views.updateInfo.setOnClickListener {
            updateUser()
        }
    }

    override fun invalidate(): Unit = withState(homeViewModel){
        when(stateModify) {
            GET_BLOCK_ID -> handleGetBlockUser(it)
            UPDATE_ID_USER -> handleUpdateUser(it)
        }
    }

    private fun handleUpdateUser(state: HomeViewState) {
        when(val update = state.updateUser){
            is Success -> {
                showToast(requireContext(),getString(R.string.update_user_successfully))
                val bundle = bundleOf(UserInfoFragment.UPDATE_ID to update.invoke().id)
                findNavController().navigate(R.id.userInfoFragment, bundle)
            }
            is Fail -> {
                update.error.message?.let {checkError(it)}
            }
            else -> {}
        }
    }

    private fun handleGetBlockUser(state: HomeViewState) {
        when(val blockUser = state.lockUser){
            is Success -> {
                views.user = blockUser.invoke()
            }
            is Fail -> {
                blockUser.error.message?.let { checkError(it) }
            }
            else -> {}
        }
    }

    private fun updateUser(){
        val updateUser = views.user?.copy(
            firstName = views.editFirstName.text.toString(),
            lastName = views.editLastName.text.toString(),
            displayName = views.editDisplayName.text.toString(),
            dob = views.editDob.text.toString(),
            gender = views.editGender.text.toString(),
            email = views.editEmail.text.toString(),
            university = views.editUniversity.text.toString(),
            year = views.editStudentYear.text.toString().toInt()
        )
        updateUser?.id?.let {id ->
            homeViewModel.handle(HomeViewAction.UpdateUser(updateUser, id))
            stateModify = UPDATE_ID_USER
        }
    }
}