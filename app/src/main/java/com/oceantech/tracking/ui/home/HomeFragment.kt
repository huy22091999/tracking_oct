package com.oceantech.tracking.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.databinding.FragmentHomeBinding
import com.oceantech.tracking.ui.home.adapter.UserAdapter
import com.oceantech.tracking.ui.item_decoration.ItemDecoration
import javax.inject.Inject

@SuppressLint( "LogNotTimber")
class HomeFragment @Inject constructor(): TrackingBaseFragment<FragmentHomeBinding>() {

    private val viewModel: HomeViewModel by activityViewModel()
    private lateinit var usersRV: RecyclerView
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

        viewModel.observeViewEvents {
            handleEvent(it)
        }

        usersRV = views.usersRV
        userAdapter = UserAdapter()
        usersRV.addItemDecoration(ItemDecoration(20))
        usersRV.layoutManager = LinearLayoutManager(requireContext())
        usersRV.adapter = userAdapter

    }

    private fun handleEvent(it: HomeViewEvent) {
        when (it) {
            is HomeViewEvent.ResetLanguage -> {

            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun invalidate(): Unit = withState(viewModel){
        when(it.allUsers){
            is Loading -> {
                views.userPB.visibility = View.VISIBLE
                views.usersRV.visibility = View.GONE
            }
            is Success -> {
                views.userPB.visibility = View.GONE
                views.usersRV.visibility = View.VISIBLE
                userAdapter.setListUsers(it.allUsers.invoke())
                userAdapter.notifyDataSetChanged()
            }
            is Fail -> {
                Log.i("User", it.allUsers.error.toString())
            }
        }
    }

}