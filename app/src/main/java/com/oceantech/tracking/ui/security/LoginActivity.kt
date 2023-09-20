package com.oceantech.tracking.ui.security

import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.airbnb.mvrx.viewModel
import com.oceantech.tracking.TrackingApplication
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseActivity
import com.oceantech.tracking.databinding.ActivityLoginBinding
import com.oceantech.tracking.utils.addFragmentToBackstack
import javax.inject.Inject

class LoginActivity : TrackingBaseActivity<ActivityLoginBinding>(), SecurityViewModel.Factory {

    val viewModel: SecurityViewModel by viewModel()

    @Inject
    lateinit var securityviewmodelFactory: SecurityViewModel.Factory
    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TrackingApplication).trackingComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(views.root)
        setupToolbar()

        supportFragmentManager.commit {
            add<LoginFragment>(R.id.frame_layout)
        }
        viewModel.observeViewEvents {
            if (it != null) {
                handleEvent(it)
            }
        }
        print(viewModel.getString())

    }

    private fun setupToolbar() {

        views.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        setSupportActionBar(views.toolbar)
        supportActionBar?.title = "";

        supportFragmentManager.addOnBackStackChangedListener {
            var count: Int = supportFragmentManager.backStackEntryCount
            handleHideAppbar(count != 0)
        }
    }

    private fun handleEvent(event: SecurityViewEvent) {
        when (event) {
            is SecurityViewEvent.ReturnSigninEvent -> {
                addFragmentToBackstack(R.id.frame_layout, SigninFragment::class.java)
            }

            is SecurityViewEvent.ReturnShowToolBar -> {
                handleHideAppbar(event.isVisible)
            }

            is SecurityViewEvent.ReturnResetpassEvent -> {
                addFragmentToBackstack(R.id.frame_layout, ResetPasswordFragment::class.java)
            }

        }
    }

    override fun getBinding(): ActivityLoginBinding {
        return ActivityLoginBinding.inflate(layoutInflater)
    }

    private fun handleHideAppbar(isVisible: Boolean) {

        if (isVisible) {
            views.appbar.animate().translationX(0F * views.appbar.width).start()
        } else {
            views.appbar.animate().translationX(1F * views.appbar.width).start()
        }
    }

    override fun create(initialState: SecurityViewState): SecurityViewModel {
        return securityviewmodelFactory.create(initialState)
    }

}