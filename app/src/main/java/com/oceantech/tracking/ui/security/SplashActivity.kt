package com.oceantech.tracking.ui.security

import android.content.Intent
import android.os.Bundle
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.viewModel
import com.oceantech.tracking.TrackingApplication
import com.oceantech.tracking.core.TrackingBaseActivity
import com.oceantech.tracking.databinding.ActivitySplashBinding
import com.oceantech.tracking.ui.MainActivity
import com.oceantech.tracking.utils.handleLogOut
import javax.inject.Inject
//done
class SplashActivity : TrackingBaseActivity<ActivitySplashBinding>(), SecurityViewModel.Factory {

    private val viewModel: SecurityViewModel by viewModel()

    @Inject
    lateinit var securityViewModelFactory: SecurityViewModel.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TrackingApplication).trackingComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(views.root)
        viewModel.subscribe(this) {
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
                handleLogOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    override fun getBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun create(initialState: SecurityViewState): SecurityViewModel {
        return securityViewModelFactory.create(initialState)
    }
}