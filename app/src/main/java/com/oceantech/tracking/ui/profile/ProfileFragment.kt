package com.oceantech.tracking.ui.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.oceantech.tracking.TrackingApplication
import com.oceantech.tracking.core.TrackingBaseFragment

import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.databinding.FragmentProfileBinding
import com.oceantech.tracking.ui.security.LoginActivity
import javax.inject.Inject

class ProfileFragment : TrackingBaseFragment<FragmentProfileBinding>() {
    var user: User? = null
    var isEdit = false

    @Inject
    lateinit var sessionManager: SessionManager


    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileBinding {
        return FragmentProfileBinding.inflate(inflater, container, false);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application.applicationContext as TrackingApplication).trackingComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        views.setting.logout.setOnClickListener {
            sessionManager.let { it.clearAuthToken() }
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        views.setting.exit.setOnClickListener {
            val homeIntent = Intent(Intent.ACTION_MAIN)
            homeIntent.addCategory(Intent.CATEGORY_HOME)
            homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(homeIntent)
        }
    }

}