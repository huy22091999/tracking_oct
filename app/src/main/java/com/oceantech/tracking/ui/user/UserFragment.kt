package com.oceantech.tracking.ui.user

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
        adapter = UserAdapter(requireContext(), users, action)

        views.users.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapter
        }

        viewModel.observeViewEvents {
            handleEvent(it)
        }
    }

    private fun handleEvent(it: HomeViewEvent) {
        when(it){
            is HomeViewEvent.ResetLanguege -> {
                views.title.text = requireContext().getString(R.string.users)
                viewModel.handle(HomeViewAction.GetAllUsers)
            }
        }
    }

    private val action:(User) -> Unit = { user ->
        if(role == "ROLE_ADMIN"){
            viewModel.handleReturnDetailUser(user)
        }
    }

    override fun invalidate():Unit = withState(viewModel){
        when(it.allUsers){
            is Success -> {
                it.allUsers.invoke()?.let { data ->
                    users = data
                    val activeUsers: List<User> = users.filter { user ->
                        user.active == true
                    }
                    adapter = UserAdapter(requireContext(), activeUsers, action)
                    views.users.adapter = adapter
                }
            }
            is Fail -> {
            }
            is Loading -> {
            }
        }
        when(it.userCurrent){
            is Success -> {
                it.userCurrent?.invoke().let { user ->
                    role = user.roles?.last()?.authority.toString()
                }
            }
        }
    }
}