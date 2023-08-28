package com.oceantech.tracking.ui.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.airbnb.mvrx.activityViewModel
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.FragmentUsersBinding
import com.oceantech.tracking.ui.users.adapter.UserAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class UsersFragment : TrackingBaseFragment<FragmentUsersBinding>() {
    private val viewModel: UserViewModel by activityViewModel()
    private lateinit var adapter: UserAdapter
    private lateinit var mListUser: List<User>
    private lateinit var progressBar: ProgressBar

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentUsersBinding {
        return FragmentUsersBinding.inflate(layoutInflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.handle(UserViewAction.GetListUser)
        initUI();
    }

    //fun initUI
    private fun initUI() {
        mListUser = listOf()
        progressBar = ProgressBar(requireContext())
        adapter = UserAdapter(requireContext(), action)
        views.users.adapter = adapter
        adapter.addLoadStateListener {
                loadState ->
            if (loadState.refresh is LoadState.Loading) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
                // getting the error
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
        viewModel.handleReturnDetailUser(user = it)
    }


}