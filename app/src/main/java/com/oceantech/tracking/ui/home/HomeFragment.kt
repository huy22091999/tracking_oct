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

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        views.tvTitle.text = getString(R.string.title_home)
    }


}