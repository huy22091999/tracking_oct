package com.oceantech.tracking.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.databinding.FragmentHomeBinding
import com.oceantech.tracking.ui.home.user.UserInfoFragment
import com.oceantech.tracking.ui.home.user.adapter.UserAdapter
import com.oceantech.tracking.utils.checkError
import com.oceantech.tracking.utils.registerNetworkReceiver
import com.oceantech.tracking.utils.setupRecycleView
import com.oceantech.tracking.utils.unregisterNetworkReceiver
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@SuppressLint("LogNotTimber", "NotifyDataSetChanged")
@AndroidEntryPoint
class HomeFragment @Inject constructor() : TrackingBaseFragment<FragmentHomeBinding>() {

    private val viewModel: HomeViewModel by activityViewModel()
    private lateinit var userAdapter: UserAdapter

    private var stateUser: Int = 0
    companion object{
        private const val GET_ALL_USER = 1
        private const val GET_USER_CURRENT = 2
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        registerNetworkReceiver {
            viewModel.handle(HomeViewAction.GetCurrentUser)
            stateUser = GET_USER_CURRENT
        }
        super.onCreate(savedInstanceState)

    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userAdapter = UserAdapter {user ->
            val bundle = Bundle().also {
                user.id?.let { id ->
                    it.putInt(UserInfoFragment.UPDATE_ID, id )
                }
            }
            findNavController().navigate(R.id.userInfoFragment, bundle)
        }
        setupRecycleView(views.usersRV, userAdapter, requireContext(), distance = 10)
        viewModel.onEach {
            views.userPB.isVisible = it.isLoading() || it.allUsers is Fail
            views.usersRV.isVisible = it.allUsers is Success
        }
    }

    override fun invalidate(): Unit = withState(viewModel) {
        when (stateUser) {
            GET_ALL_USER -> handleGetAllUser(it)
            GET_USER_CURRENT -> handleGetCurrentUser(it)
        }

    }

    private fun handleGetAllUser(state: HomeViewState){
        when(val allUsers = state.allUsers){
            is Success -> {
                allUsers.invoke().let { users ->
                    userAdapter.list = users
                    userAdapter.notifyDataSetChanged()
                }

            }
            is Fail -> {
                allUsers.error.message?.let { error ->
                    checkError(error)
                }

            }
            else -> {}
        }
    }

    private fun handleGetCurrentUser(state: HomeViewState){
        when(val user = state.userCurrent){
            is Success -> {
                user.invoke().roles?.first()?.authority?.let {
                    userAdapter.setAuthority(it)
                }
                viewModel.handle(HomeViewAction.GetAllUsers)
                stateUser = GET_ALL_USER
            }
            is Fail -> {
                user.error.message?.let { error ->
                    checkError(error)
                }

            }
            else -> {}
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterNetworkReceiver()
    }
}