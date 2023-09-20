package com.oceantech.tracking.ui.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.airbnb.mvrx.activityViewModel
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.FragmentUsersBinding
import com.oceantech.tracking.ui.users.adapter.UserAdapter
import kotlinx.coroutines.flow.collectLatest
//done
class UsersFragment : TrackingBaseFragment<FragmentUsersBinding>() {
    private val viewModel: UserViewModel by activityViewModel()
    //data
    private lateinit var mListUser: List<User>
    //views
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: UserAdapter

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentUsersBinding {
        return FragmentUsersBinding.inflate(layoutInflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.handle(UserViewAction.GetListUser)
        initUI();
        listenEvent()
    }

    private fun initUI() {
        mListUser = listOf()
        progressBar = ProgressBar(requireContext())
        adapter = UserAdapter(action)
        views.users.adapter = adapter
        adapter.addLoadStateListener {
                loadState ->
            if (loadState.refresh is LoadState.Loading) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
                val error = when {
                    loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                    loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                    loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                    else -> null
                }
                error?.let {
                    Toast.makeText(requireContext(), it.error.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        lifecycleScope.launchWhenCreated {
            viewModel.handleGetListUser().collectLatest {
                adapter.submitData(it)
            }
        }
    }

    //fun callback
    private val action: (User) -> Unit = {
        val action = UsersFragmentDirections.actionNavUsersFragmentToUserDetailsFragment(it)
        findNavController().navigate(action)
    }

    private fun listenEvent() {
        views.findingUser.setOnClickListener {
            findNavController().navigate(R.id.action_nav_usersFragment_to_searchUserFragment)
        }
    }

}