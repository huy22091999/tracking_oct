package com.oceantech.tracking.ui.security

import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.viewModel
import com.oceantech.tracking.TrackingApplication
import com.oceantech.tracking.core.TrackingBaseActivity
import com.oceantech.tracking.core.TrackingViewModel
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.databinding.ActivitySplashBinding
import com.oceantech.tracking.ui.MainActivity
import java.util.Locale
import javax.inject.Inject


class SplashActivity : TrackingBaseActivity<ActivitySplashBinding>(), SecurityViewModel.Factory {

    private val viewModel: SecurityViewModel by viewModel()

    @Inject
    lateinit var securityViewModelFactory: SecurityViewModel.Factory

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TrackingApplication).trackingComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(views.root)
        viewModel.handle(SecurityViewAction.GetUserCurrent)
        setupSettingApp()
        viewModel.subscribe(this) {
            handleStateChange(it)
        }
    }

    private fun setupSettingApp() {
        // setting language
        sessionManager.fetchLanguage().let {
            val res: Resources = resources
            val dm: DisplayMetrics = res.displayMetrics
            val conf: Configuration = res.configuration
            val myLocale = Locale(it)
            conf.setLocale(myLocale)
            res.updateConfiguration(conf, dm)
        }
    }

    private fun handleStateChange(it: SecurityViewState) {
        when (it.userCurrent) {
            is Success -> {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }

            is Fail -> {
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