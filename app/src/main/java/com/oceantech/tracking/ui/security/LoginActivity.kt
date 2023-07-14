package com.oceantech.tracking.ui.security

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.airbnb.mvrx.viewModel
import com.oceantech.tracking.TrackingApplication
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseActivity
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.ActivityLoginBinding
import javax.inject.Inject

class LoginActivity : TrackingBaseActivity<ActivityLoginBinding>(), SecurityViewModel.Factory {

    val viewModel: SecurityViewModel by viewModel()
    private lateinit var navController: NavController

    @Inject
    lateinit var securityviewmodelFactory: SecurityViewModel.Factory
    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TrackingApplication).trackingComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(views.root)
        navController = findNavController(R.id.frame_layout)
//        supportFragmentManager.commit {
//            add<LoginFragment>(R.id.frame_layout)
//        }
        viewModel.observeViewEvents {
            if (it != null) {
                handleEvent(it)
            }
        }
        print(viewModel.getString())
    }

    private fun handleEvent(event: SecurityViewEvent) {
        when (event) {
            is SecurityViewEvent.ReturnSigninEvent -> {
                navigateTo(R.id.signinFragment)
            }
            is SecurityViewEvent.ReturnResetpassEvent -> {
                navigateTo(R.id.resetPasswordFragment)
            }
            is SecurityViewEvent.ReturnLoginEvent -> {
                navigateTo(R.id.loginFragment)
            }
            is SecurityViewEvent.ReturnNextSignInEvent -> {
                navigateTo(R.id.nextSigninFragment, event.user)
            }
        }
    }

    override fun getBinding(): ActivityLoginBinding {
        return ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun create(initialState: SecurityViewState): SecurityViewModel {
        return securityviewmodelFactory.create(initialState)
    }

    private fun navigateTo(fragmentId: Int, user: User? = null) {
        if(user != null){
            val direction = SigninFragmentDirections.actionSigninFragmentToNextSigninFragment(user)
            navController.navigate(direction)
        } else {
            navController.navigate(fragmentId)
        }
    }
}