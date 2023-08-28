package com.oceantech.tracking.ui.users

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.airbnb.mvrx.*
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.FragmentUsersBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class UsersFragment @Inject constructor() : TrackingBaseFragment<FragmentUsersBinding>() {

    private val userViewModel: UserViewModel by activityViewModel()

    lateinit var adapter : UserAdapter



    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentUsersBinding {
        return FragmentUsersBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel.handle(UsersViewAction.RefeshUserAction(lifecycleScope))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRCV()

        listennerUiClick()
    }
    
    private fun setUpRCV() {
        adapter = UserAdapter {
            userViewModel.handleReturnDetailUser(it)
        }
        views.rcvUsers.setHasFixedSize(true)
        views.rcvUsers.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        views.rcvUsers.adapter = adapter
    }

    private fun listennerUiClick() {
        views.swipeLayout.setOnRefreshListener {
            userViewModel.handle(UsersViewAction.RefeshUserAction(lifecycleScope))
        }
    }

    override fun invalidate() = withState(userViewModel) {
        when (it.pageUsers) {
            is Success ->{
                adapter.submitData(lifecycle, it.pageUsers.invoke())
                views.swipeLayout.isRefreshing = false
            }

            is Fail ->{
                Timber.e("UsersFragment invalidate Fail:")
            }
        }

        views.progressLoading.isVisible = it.isLoadding()

    }





}