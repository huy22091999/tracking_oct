package com.oceantech.tracking.ui.security

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.viewModel
import com.oceantech.tracking.TrackingApplication
import com.oceantech.tracking.core.TrackingBaseActivity
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.databinding.ActivitySplashBinding
import com.oceantech.tracking.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : TrackingBaseActivity<ActivitySplashBinding>(), SecurityViewModel.Factory {

    private val viewModel: SecurityViewModel by viewModel()

    @Inject
    lateinit var securityViewModelFactory: SecurityViewModel.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(views.root)
        viewModel.handle(SecurityViewAction.GetUserCurrent)
        viewModel.onEach {
            handleStateChange(it)
        }

    }

    private fun handleStateChange(it: SecurityViewState) {
        when (it.userCurrent) {
            is Success -> {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }

            is Fail -> {
                SessionManager(applicationContext).also {
                    it.deleteAuthToken()
                }
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }

            else -> {}
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun getBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun create(initialState: SecurityViewState): SecurityViewModel {
        return securityViewModelFactory.create(initialState)
    }
}