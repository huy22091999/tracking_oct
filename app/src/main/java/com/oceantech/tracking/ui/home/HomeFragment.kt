package com.oceantech.tracking.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.network.UserApi
import com.oceantech.tracking.databinding.FragmentHomeBinding
import com.oceantech.tracking.ui.MainActivity
import com.oceantech.tracking.ui.security.SecurityViewAction
import com.oceantech.tracking.ui.security.SecurityViewEvent
import retrofit2.Call
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject
import javax.security.auth.callback.Callback

class HomeFragment @Inject constructor(val api: UserApi) :
    TrackingBaseFragment<FragmentHomeBinding>() {

    private val viewModel: HomeViewModel by activityViewModel()
    private lateinit var adapter : HomeAdapter

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.handle(HomeViewAction.GetAllUser)
        adapter = HomeAdapter()
        views.rcvUser.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        views.rcvUser.adapter = adapter
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun invalidate(): Unit = withState(viewModel) {
        when(it.asyncListUser){
            is Success -> {
                adapter.setData(it.asyncListUser.invoke())
                adapter.notifyDataSetChanged()
            }
            is Fail -> {
                Timber.e("getAllUser : fail")
            }
        }
    }
}