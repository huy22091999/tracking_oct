package com.oceantech.tracking.ui.security

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.viewModel
import com.oceantech.tracking.R
import com.oceantech.tracking.TrackingApplication
import com.oceantech.tracking.core.TrackingBaseActivity
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.databinding.ActivitySplashBinding
import com.oceantech.tracking.ui.MainActivity
import com.oceantech.tracking.utils.initialAlertDialog
import java.util.Locale
import javax.inject.Inject


class SplashActivity : TrackingBaseActivity<ActivitySplashBinding>(), SecurityViewModel.Factory {
    private val viewModel: SecurityViewModel by viewModel()
    @Inject
    lateinit var securityViewModelFactory: SecurityViewModel.Factory
    private lateinit var dialog:AlertDialog
    private var isDialogShowing: Boolean = false
    private var hasCheckVersion:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val sessionManager = SessionManager(this@SplashActivity)
        val modeTheme = sessionManager.fetchAppTheme()?.toInt()
        if(modeTheme != null){
            AppCompatDelegate.setDefaultNightMode(modeTheme)
        }

        (applicationContext as TrackingApplication).trackingComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(views.root)

        views.versionLabel.text = "Version: ${packageManager.getPackageInfo(packageName, 0).versionName}"

        val lang = sessionManager.fetchAppLanguage()
        val res: Resources = resources
        val dm: DisplayMetrics = res.displayMetrics
        val conf: Configuration = res.configuration
        val myLocale = Locale(lang)
        conf.setLocale(myLocale)
        res.updateConfiguration(conf, dm)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(android.Manifest.permission.INTERNET,android.Manifest.permission.POST_NOTIFICATIONS),
                1001
            )
        } else {
            viewModel.handle(SecurityViewAction.GetConfigApp)

            viewModel.subscribe(this) {
                handleStateChange(it)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
            viewModel.handle(SecurityViewAction.GetConfigApp)

            viewModel.subscribe(this) {
                handleStateChange(it)
            }
        } else {
            Toast.makeText(
                this,
                getString(R.string.permission),
                Toast.LENGTH_SHORT
            ).show()
            val homeIntent = Intent(Intent.ACTION_MAIN)
            homeIntent.addCategory(Intent.CATEGORY_HOME)
            homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(homeIntent)
        }
    }

    override fun onResume() {
        super.onResume()
        if(hasCheckVersion){
            viewModel.handle(SecurityViewAction.GetUserCurrent)
        }
    }

    private val moveToChPlay:()->Unit = {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/search?q=spotify&c=apps&hl=vi&gl=US")))
        } catch (e:ActivityNotFoundException){

        }
    }

    private val getCurrentUser:()->Unit={
        viewModel.handle(SecurityViewAction.GetUserCurrent)
    }
    private fun handleStateChange(it: SecurityViewState) {
        when(it.asyncConfigApp){
            is Success -> {
                it.asyncConfigApp.invoke().let { version ->
                    hasCheckVersion = true
                    val appVersion = packageManager.getPackageInfo(packageName, 0).versionName
                    if(version.versionName.toString() != appVersion){
                        dialog = initialAlertDialog(this,
                            moveToChPlay, getCurrentUser,
                            getString(R.string.update_notify),
                            getString(R.string.update_now),
                            getString(R.string.remind_later))

                        if(!isFinishing && !isDialogShowing){
                            dialog.show()
                            isDialogShowing = true
                        }
                    } else {
                        viewModel.handle(SecurityViewAction.GetUserCurrent)
                    }
                }
            }
        }
        when (it.userCurrent) {
            is Success -> {
                moveToMain()
                finish()
            }

            is Fail -> {
                moveToLogin()
                val sessionManager = SessionManager(this@SplashActivity)
                sessionManager.clearAuthToken()
                finish()
            }
        }
    }

    private fun moveToMain(){
        val sessionManager = SessionManager(this@SplashActivity)
        val modeTheme = sessionManager.fetchAppTheme()?.toInt()
        if(modeTheme != null){
            AppCompatDelegate.setDefaultNightMode(modeTheme)
            val i = Intent(this@SplashActivity, MainActivity::class.java)
            overridePendingTransition(0, 0)
            startActivity(i)
            overridePendingTransition(0, 0)
        }
    }

    private fun moveToLogin(){
        val sessionManager = SessionManager(this@SplashActivity)
        val modeTheme = sessionManager.fetchAppTheme()?.toInt()
        if(modeTheme != null){
            AppCompatDelegate.setDefaultNightMode(modeTheme)
            val i = Intent(this@SplashActivity, LoginActivity::class.java)
            overridePendingTransition(0, 0)
            startActivity(i)
            overridePendingTransition(0, 0)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        if(dialog != null || dialog.isShowing){
            dialog.cancel()
        }
    }
    override fun getBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun create(initialState: SecurityViewState): SecurityViewModel {
        return securityViewModelFactory.create(initialState)
    }
}