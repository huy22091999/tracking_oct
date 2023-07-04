package com.oceantech.tracking.ui.security

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.viewModel
import com.oceantech.tracking.BuildConfig
import com.oceantech.tracking.R
import com.oceantech.tracking.TrackingApplication
import com.oceantech.tracking.core.TrackingBaseActivity
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.databinding.ActivitySplashBinding
import com.oceantech.tracking.databinding.DialogLoginBinding
import com.oceantech.tracking.ui.MainActivity
import javax.inject.Inject


class SplashActivity : TrackingBaseActivity<ActivitySplashBinding>(), SecurityViewModel.Factory {
    private val viewModel: SecurityViewModel by viewModel()
    @Inject
    lateinit var securityViewModelFactory: SecurityViewModel.Factory
    private lateinit var dialog:AlertDialog
    private var isDialogShowing: Boolean = false
    private var hasCheckVersion:Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TrackingApplication).trackingComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(views.root)

        viewModel.handle(SecurityViewAction.GetConfigApp)

        viewModel.subscribe(this) {
            handleStateChange(it)
        }


    }

    override fun onResume() {
        super.onResume()

        if(hasCheckVersion){
            viewModel.handle(SecurityViewAction.GetUserCurrent)
        }
    }

    private fun createDialog(): AlertDialog {
        val builder = AlertDialog.Builder(this@SplashActivity)
        val view: ViewBinding = DialogLoginBinding.inflate(LayoutInflater.from(this@SplashActivity))
        builder.setView(view.root)

        val alertDialog = builder.create()

        with(view as DialogLoginBinding){
            view.dialogTitle.text = getString(R.string.update_notify)
            view.returnSignIn.text = getString(R.string.update_now)
            view.back.text = getString(R.string.remind_later)
            view.returnSignIn.setOnClickListener {
                if(alertDialog.isShowing){
                    alertDialog.dismiss()
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/search?q=spotify&c=apps&hl=vi&gl=US")))
                    } catch (e:ActivityNotFoundException){

                    }
                    alertDialog.cancel()
                }
            }
            view.back.setOnClickListener {
                if(alertDialog.isShowing){
                    alertDialog.dismiss()
                    alertDialog.cancel()
                    viewModel.handle(SecurityViewAction.GetUserCurrent)
                }
            }
        }
        return alertDialog
    }

    private fun handleStateChange(it: SecurityViewState) {
        when(it.asyncConfigApp){
            is Success -> {
                it.asyncConfigApp.invoke().let { version ->
                    hasCheckVersion = true
                    val appVersion = packageManager.getPackageInfo(packageName, 0).versionName
                    Log.i("Version app:", version.versionName.toString())
                    Log.i("Version app:", appVersion)
                    if(version.versionName.toString() != appVersion){
                        dialog = createDialog()
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
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }

            is Fail -> {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
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