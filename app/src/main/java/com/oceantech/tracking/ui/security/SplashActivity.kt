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
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.data.network.SessionManager.Companion.ROLE_ADMIN
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

    private val role_admin = ROLE_ADMIN

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TrackingApplication).trackingComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(views.root)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels
        val targetWidth = (screenWidth * 0.3).toInt()
        val targetHeight = (screenHeight * 0.3).toInt()
        val imageView = views.imgLogo
        val layoutParams = imageView.layoutParams
        layoutParams.width = targetWidth
        layoutParams.height = targetHeight
        imageView.layoutParams = layoutParams


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
                val user = it.userCurrent.invoke()
                it.userCurrent.invoke().let { user ->
                    val role = user?.roles?.last()?.authority.toString()
//                    if (role == role_admin)
//                        sessionManager.saveRoleAdmin(true)
                    viewModel.handle(SecurityViewAction.SaveRole(role))
                    viewModel.handle(SecurityViewAction.SaveFullName(user?.displayName.toString()))
                }
                startActivity(Intent(this, MainActivity::class.java).apply {
                    putExtra(MainActivity.EXTRA_USER, user)
                })
            }

            is Fail -> {
                sessionManager.clearAuthToken()
                sessionManager.clearRole()
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