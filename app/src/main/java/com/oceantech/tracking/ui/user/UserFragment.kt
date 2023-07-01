package com.oceantech.tracking.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentUserBinding
        = FragmentUserBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        users = listOf()
        adapter = UserAdapter(requireContext(), users)

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

    override fun invalidate():Unit = withState(viewModel){
        when(it.allUsers){
            is Success -> {
                it.allUsers.invoke()?.let { data ->
                    users = data
                    adapter = UserAdapter(requireContext(), data)
                    views.users.adapter = adapter
                }
                dismissLoadingDialog()
            }
            is Fail -> {
                dismissLoadingDialog()
            }
            is Loading -> {
                showLoadingDialog()
            }
        }
    }
}