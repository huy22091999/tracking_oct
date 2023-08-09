package com.oceantech.tracking.ui.personal

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.databinding.FragmentPersonalBinding
import com.oceantech.tracking.ui.home.HomeViewAction
import com.oceantech.tracking.ui.home.HomeViewModel
import com.oceantech.tracking.ui.security.LoginActivity
import com.oceantech.tracking.utils.checkError
import com.oceantech.tracking.utils.handleBackPressedEvent
import com.oceantech.tracking.utils.handleLogOut
import com.oceantech.tracking.utils.registerNetworkReceiver
import com.oceantech.tracking.utils.unregisterNetworkReceiver

class PersonalFragment : TrackingBaseFragment<FragmentPersonalBinding>() {

    private val viewModel: HomeViewModel by activityViewModel()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerNetworkReceiver {
            viewModel.handle(HomeViewAction.GetCurrentUser)
        }

    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPersonalBinding {
        return FragmentPersonalBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onEach {
            views.personPB.isVisible = it.isLoading() || it.userCurrent is Fail
            views.personInfor.isVisible = it.userCurrent is Success
            views.personFullName.isVisible = it.userCurrent is Success
            views.personBtn.isVisible = it.userCurrent is Success
        }
        views.btnLogOut.setOnClickListener {
            requireActivity().handleLogOut()
        }
        views.btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.modifyPersonalFragment)
        }
    }

    override fun invalidate(): Unit = withState(viewModel) {
        when (it.userCurrent) {
            is Success -> {
                bindUser(it.userCurrent.invoke())
            }
            is Fail -> {
                it.userCurrent.error.message?.let { it1 -> checkError(it1) }
            }

            else -> {}
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindUser(user: User) {
        views.txtPersonId.text = "Id: ${user.id}"
        views.profileDisplayName.text = "${user.displayName}"
        views.txtPersonUsermame.text = "Username: ${user.username}"
        views.txtPersonDisplayName.text = "Display Name: ${user.displayName}"
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterNetworkReceiver()
    }
}