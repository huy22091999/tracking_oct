package com.oceantech.tracking.ui.security

import android.os.Bundle
import android.view.View
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.airbnb.mvrx.viewModel
import com.oceantech.tracking.TrackingApplication
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseActivity
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.databinding.ActivityLoginBinding
import com.oceantech.tracking.ui.security.onbroading.ViewPagerFragment
import com.oceantech.tracking.utils.addFragmentToBackstack
import com.oceantech.tracking.utils.changeDarkMode
import com.oceantech.tracking.utils.changeLanguage
import javax.inject.Inject
//done
class LoginActivity : TrackingBaseActivity<ActivityLoginBinding>(), SecurityViewModel.Factory {

    val viewModel: SecurityViewModel by viewModel()

    @Inject
    lateinit var securityViewModelFactory: SecurityViewModel.Factory

    @Inject
    lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TrackingApplication).trackingComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(views.root)
        setupUi()
    }

    private fun setupUi() {
        supportFragmentManager.commit {
            if (sessionManager.getOnBoardingFinished()) {
                add<LoginFragment>(R.id.frame_layout)
            } else {
                add<ViewPagerFragment>(R.id.frame_layout)
            }
        }
        viewModel.observeViewEvents {
            if (it != null) {
                handleEvent(it)
            }
        }
        print(viewModel.getString())
        changeDarkMode(sessionManager.getDarkMode())
        changeLanguage(sessionManager.getLanguage())
    }

    private fun handleEvent(event: SecurityViewEvent) {
        when (event) {
            is SecurityViewEvent.ReturnInfoRegisterEvent -> {
                addFragmentToBackstack(R.id.frame_layout, InfoRegisterFragment::class.java)
            }

            is SecurityViewEvent.ReturnSignUpEvent -> {
                val user = event.user
                val bundle = Bundle()
                bundle.putSerializable("info_user", user)
                addFragmentToBackstack(
                    frameId = R.id.frame_layout,
                    bundle = bundle,
                    fragmentClass = SignUpFragment::class.java,
                )
            }

            is SecurityViewEvent.ReturnResetPassEvent -> {
                addFragmentToBackstack(R.id.frame_layout, ResetPasswordFragment::class.java)
            }

            is SecurityViewEvent.ReturnLoginEvent -> {
                addFragmentToBackstack(R.id.frame_layout, LoginFragment::class.java)
            }

        }

    }

    override fun getBinding(): ActivityLoginBinding {
        return ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun create(initialState: SecurityViewState): SecurityViewModel {
        return securityViewModelFactory.create(initialState)
    }

}