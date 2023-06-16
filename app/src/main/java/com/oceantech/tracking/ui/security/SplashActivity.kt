package com.oceantech.tracking.ui.security

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.viewModel
import com.oceantech.tracking.TrackingApplication
import com.oceantech.tracking.core.TrackingBaseActivity
import com.oceantech.tracking.databinding.ActivitySplashBinding
import com.oceantech.tracking.ui.MainActivity
import javax.inject.Inject


class SplashActivity() : TrackingBaseActivity<ActivitySplashBinding>(), SecurityViewModel.Factory {

    private val viewModel: SecurityViewModel by viewModel()

    @Inject
    lateinit var securityViewModelFactory: SecurityViewModel.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TrackingApplication).trackingComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(views.root)
        viewModel.handle(SecurityViewAction.GetUserCurrent)
        viewModel.subscribe(this) {
            handleStateChange(it)
        }
    }

    private fun handleStateChange(it: SecurityViewState) {
        when (it.userCurrent) {
            is Success -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("user",it.userCurrent.invoke())
                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(intent)
                    finish()
                },2000)
            }

            is Fail -> {
                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                },2000)
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