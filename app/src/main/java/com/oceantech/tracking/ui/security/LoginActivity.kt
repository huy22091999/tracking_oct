package com.oceantech.tracking.ui.security

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.airbnb.mvrx.viewModel
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseActivity
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.databinding.ActivityLoginBinding
import com.oceantech.tracking.utils.TrackingContextWrapper
import com.oceantech.tracking.utils.addFragmentToBackstack
import com.oceantech.tracking.utils.changeDarkMode
import com.oceantech.tracking.utils.registerNetworkReceiver
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject
@AndroidEntryPoint

class LoginActivity : TrackingBaseActivity<ActivityLoginBinding>(), SecurityViewModel.Factory {

    private val viewModel: SecurityViewModel by viewModel()


    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var securityViewModelFactory: SecurityViewModel.Factory
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(views.root)
        changeDarkMode(sessionManager.getDarkMode())
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleEvent(event: SecurityViewEvent) {
        when (event) {
            is SecurityViewEvent.ReturnSigninEvent -> {
                addFragmentToBackstack(R.id.frame_layout, SigninFragment::class.java)
            }

            is SecurityViewEvent.ReturnResetpassEvent -> {
                addFragmentToBackstack(R.id.frame_layout, ResetPasswordFragment::class.java)
            }

        }
    }

    override fun getBinding(): ActivityLoginBinding {
        return ActivityLoginBinding.inflate(layoutInflater)
    }


    override fun create(initialState: SecurityViewState): SecurityViewModel {
        return securityViewModelFactory.create(initialState)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun attachBaseContext(context: Context?) {
        val prefs = context?.getSharedPreferences(
            context.getString(R.string.app_name),
            Context.MODE_PRIVATE
        )
        val language = prefs?.getString(SessionManager.LANGUAGE, Locale.getDefault().language)
        super.attachBaseContext(language?.let { TrackingContextWrapper.wrap(context, it) })
    }
}