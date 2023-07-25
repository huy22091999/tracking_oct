package com.oceantech.tracking.ui.user

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.FragmentUserBinding
import com.oceantech.tracking.ui.home.HomeViewAction
import com.oceantech.tracking.ui.home.HomeViewEvent
import com.oceantech.tracking.ui.home.HomeViewModel
import kotlinx.coroutines.flow.collectLatest

class UserFragment : TrackingBaseFragment<FragmentUserBinding>() {
    private val viewModel: HomeViewModel by activityViewModel()
    private lateinit var adapter:UserAdapter
    private lateinit var users:List<User>
    private var role:String = "ROLE_USER"

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentUserBinding
        = FragmentUserBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.handle(HomeViewAction.GetAllUsers)

        users = listOf()
        adapter = UserAdapter(requireContext(), action)

        views.users.apply {
            layoutManager = LinearLayoutManager(requireContext())
        }

        views.users.adapter = adapter.withLoadStateHeaderAndFooter(
            header = UserLoadStateAdapter { adapter.retry() },
            footer = UserLoadStateAdapter { adapter.retry() }
        )

        lifecycleScope.launchWhenCreated {
            viewModel.handleAllUsers().collectLatest {
                adapter.submitData(it)
            }
        }

        viewModel.observeViewEvents {
            handleEvent(it)
        }
    }

    private fun handleEvent(it: HomeViewEvent) {
        when(it){
            is HomeViewEvent.ResetLanguege -> {
                views.title.text = requireContext().getString(R.string.users)
                lifecycleScope.launchWhenCreated {
                    viewModel.handleAllUsers().collectLatest {
                        adapter.submitData(it)
                    }
                }
            }
        }
    }

    private val action:(User) -> Unit = { user ->
        if(role == "ROLE_ADMIN"){
            viewModel.handleReturnDetailUser(user)
        }
    }

    override fun invalidate():Unit = withState(viewModel){
        when(it.userCurrent){
            is Success -> {
                it.userCurrent?.invoke().let { user ->
                    role = user.roles?.last()?.authority.toString()
                }
            }
        }
    }
}