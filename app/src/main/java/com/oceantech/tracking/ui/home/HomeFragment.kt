package com.oceantech.tracking.ui.home

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.PageSearch
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.FragmentHomeBinding
import com.oceantech.tracking.ui.home.user.UserInfoFragment
import com.oceantech.tracking.ui.home.user.UserViewModel
import com.oceantech.tracking.ui.home.user.adapter.UserAdapter
import com.oceantech.tracking.utils.checkError
import com.oceantech.tracking.utils.registerNetworkReceiver
import com.oceantech.tracking.utils.setupRecycleView
import com.oceantech.tracking.utils.showToast
import com.oceantech.tracking.utils.unregisterNetworkReceiver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds
import kotlin.time.seconds

@SuppressLint("LogNotTimber", "NotifyDataSetChanged")
@AndroidEntryPoint
class HomeFragment @Inject constructor() : TrackingBaseFragment<FragmentHomeBinding>() {

    private val viewModel: HomeViewModel by activityViewModel()
    private lateinit var userAdapter: UserAdapter
    private lateinit var usersRV: RecyclerView
    private var stateUser: Int = 0

//    private val userViewModel: UserViewModel by lazy {
//        ViewModelProvider(this)[UserViewModel::class.java]
//    }

    companion object {
        private const val GET_USER_CURRENT = 1
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        usersRV = views.usersRV
        userAdapter = UserAdapter { user ->
            val bundle = Bundle().also {
                user.id?.let { id ->
                    it.putInt(UserInfoFragment.UPDATE_ID, id)
                }
            }
            findNavController().navigate(R.id.userInfoFragment, bundle)
        }
        setupRecycleView(usersRV, userAdapter, requireContext(), distance = 10)

        val pagerSnapHelper = LinearSnapHelper()
        pagerSnapHelper.attachToRecyclerView(usersRV)
        userAdapter.addLoadStateListener {loadState ->
            views.userPB.isVisible = loadState.refresh is LoadState.Loading
        }
    }

    override fun invalidate(): Unit = withState(viewModel) {
        when (stateUser) {
            GET_USER_CURRENT -> handleGetCurrentUser(it)
        }
    }

    private fun handleGetCurrentUser(state: HomeViewState) {
        when (val user = state.userCurrent) {
            is Success -> {
                user.invoke().roles?.first()?.authority?.let {
                    userAdapter.setAuthority(it)
                }
                lifecycleScope.launch {
                    viewModel.handleFlowData().collectLatest { pagingData ->
                        userAdapter.submitData(pagingData)
                    }
                }
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