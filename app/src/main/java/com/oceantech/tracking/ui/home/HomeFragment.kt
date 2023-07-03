package com.oceantech.tracking.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.databinding.FragmentHomeBinding
import com.oceantech.tracking.ui.home.adapter.UserAdapter
import com.oceantech.tracking.utils.checkError
import com.oceantech.tracking.utils.setupRecycleView
import com.oceantech.tracking.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.HttpException
import javax.inject.Inject

@SuppressLint("LogNotTimber")
@AndroidEntryPoint
class HomeFragment @Inject constructor() : TrackingBaseFragment<FragmentHomeBinding>() {

    private val viewModel: HomeViewModel by activityViewModel()
    private lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel.handle(HomeViewAction.GetAllUsers)
        super.onCreate(savedInstanceState)

    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userAdapter = UserAdapter()
        setupRecycleView(views.usersRV, userAdapter, requireContext(), distance = 10)
        viewModel.onEach {
            views.userPB.isVisible = it.isLoading() || it.allUsers is Fail
            views.usersRV.isVisible = !it.isLoading() && it.allUsers is Success
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun invalidate(): Unit = withState(viewModel) {
        when (it.allUsers) {
            is Success -> {
                it.allUsers.invoke().let { users ->
                    userAdapter.setListUsers(users)
                    userAdapter.notifyDataSetChanged()
                }
            }
            is Fail -> {
                it.allUsers.error.message?.let { error ->
                    checkError(error)
                }

            }
            else -> {}
        }

    }

}