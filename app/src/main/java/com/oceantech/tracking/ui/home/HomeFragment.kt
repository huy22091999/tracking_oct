package com.oceantech.tracking.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.activityViewModel
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.network.UserApi
import com.oceantech.tracking.databinding.FragmentHomeBinding
import com.oceantech.tracking.ui.MainActivity
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject
import javax.security.auth.callback.Callback

class HomeFragment @Inject constructor(val api: UserApi) :
    TrackingBaseFragment<FragmentHomeBinding>() {

    private val viewModel: HomeViewModel by activityViewModel()

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        views.homeToCategory.setOnClickListener {
            //(activity as MainActivity).navigateTo(R.id.action_FirstFragment_to_newsFragment)
        }
        viewModel.observeViewEvents {
            handleEvent(it)
        }
        api.getCurrentUserTest().enqueue(object : retrofit2.Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                Log.e("Test", "onResponse: $response")
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("Test", "onResponse: ${t.stackTrace}")
            }

        })
    }

    private fun handleEvent(it: HomeViewEvent) {
        when (it) {
            is HomeViewEvent.ResetLanguege -> {
                views.title.text = getString(R.string.home_everyone)
                views.homeToCategory.text = getString(R.string.home_button)
            }
        }
    }
}