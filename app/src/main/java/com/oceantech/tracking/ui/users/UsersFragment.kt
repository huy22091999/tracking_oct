package com.oceantech.tracking.ui.users

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.FragmentUsersBinding
import com.oceantech.tracking.ui.profile.ProfileFragmentDirections
import com.oceantech.tracking.utils.checkStatusApiRes
import timber.log.Timber
import javax.inject.Inject

class UsersFragment @Inject constructor() : TrackingBaseFragment<FragmentUsersBinding>() {
    private val usersViewModel: UsersViewModel by activityViewModel()
    lateinit var adapter: UserAdapter
    private var role: String = "ROLE_USER"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        usersViewModel.handle(UsersViewAction.RefeshUserAction(lifecycleScope))


    }

    private fun handleEvents(it: UsersViewEvent) {
        when (it) {
            is UsersViewEvent.ReturnDetailViewEvent -> {
                val action =
                    UsersFragmentDirections.actionNavUsersFragmentToInformationFragment3(it.user)
                findNavController().navigate(action)
            }
        }
    }


    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentUsersBinding {
        return FragmentUsersBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        usersViewModel.observeViewEvents {
            if (it != null) {
                handleEvents(it)
            }
        }
        setUpRCV()
    }

    private fun setUpRCV() {
        adapter = UserAdapter {
            //usersViewModel.handleReturnDetailUser(it)
            action(it)
        }
        views.recyclerView.setHasFixedSize(true)
        views.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        views.recyclerView.adapter = adapter
    }

    private val action: (User) -> Unit = { user ->
        if (role != "ROLE_USER") {
            usersViewModel.handleReturnDetailUser(user)
        }else{
            Toast.makeText(requireActivity(), "Quyền truy cập bị từ chối", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun invalidate() = withState(usersViewModel) {
        when (it.pageUsers) {
            is Success -> {
                val pageUser = it.pageUsers.invoke()
                adapter.submitData(lifecycle,pageUser)
                Timber.e("UsersFragment Success: $pageUser")
                Toast.makeText(requireContext(), getString(R.string.success), Toast.LENGTH_SHORT)
                    .show()
            }

            is Fail -> {
                Timber.e("UsersFragment invalidate Fail:")
                Toast.makeText(
                    requireContext(),
                    getString(checkStatusApiRes(it.pageUsers)),
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {}
        }
    }
}