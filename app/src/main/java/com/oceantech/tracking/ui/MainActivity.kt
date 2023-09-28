package com.oceantech.tracking.ui

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.viewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.oceantech.tracking.R
import com.oceantech.tracking.TrackingApplication
import com.oceantech.tracking.core.TrackingBaseActivity
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.databinding.ActivityMainBinding
import com.oceantech.tracking.ui.home.TestViewModel
import com.oceantech.tracking.ui.information.InformationViewModel
import com.oceantech.tracking.ui.information.InformationViewState
import com.oceantech.tracking.ui.notification.NotificationViewModel
import com.oceantech.tracking.ui.notification.NotificationViewState
import com.oceantech.tracking.ui.profile.ProfileViewEvent
import com.oceantech.tracking.ui.profile.ProfileViewModel
import com.oceantech.tracking.ui.profile.ProfileViewState
import com.oceantech.tracking.ui.security.SecurityViewAction
import com.oceantech.tracking.ui.security.SecurityViewModel
import com.oceantech.tracking.ui.security.SecurityViewState
import com.oceantech.tracking.ui.security.UserPreferences
import com.oceantech.tracking.ui.timesheet.TimeSheetViewModel
import com.oceantech.tracking.ui.timesheet.TimeSheetViewState
import com.oceantech.tracking.ui.tracking.TrackingViewModel
import com.oceantech.tracking.ui.tracking.TrackingViewState
import com.oceantech.tracking.ui.users.UserViewState
import com.oceantech.tracking.ui.users.UsersViewModel
import com.oceantech.tracking.utils.LocalHelper
import com.oceantech.tracking.utils.checkStatusApiRes
import java.util.*
import javax.inject.Inject


class MainActivity : TrackingBaseActivity<ActivityMainBinding>(),
    ProfileViewModel.Factory, InformationViewModel.Factory, UsersViewModel.Factory,
    TimeSheetViewModel.Factory, TrackingViewModel.Factory, SecurityViewModel.Factory,
    NotificationViewModel.Factory {
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "nimpe_channel_id"
        const val EXTRA_USER = "extra_user"
    }

    private val securityViewModel: SecurityViewModel by viewModel()

    private val profileViewModel: ProfileViewModel by viewModel()

    @Inject
    lateinit var profileViewModelFactory: ProfileViewModel.Factory

    @Inject
    lateinit var securityViewModelFactory: SecurityViewModel.Factory

    @Inject
    lateinit var informationViewModelFactory: InformationViewModel.Factory

    @Inject
    lateinit var usersviewmodelFactory: UsersViewModel.Factory

    @Inject
    lateinit var timeSheetViewModelFactory: TimeSheetViewModel.Factory

    @Inject
    lateinit var trackingViewModelFactory: TrackingViewModel.Factory

    @Inject
    lateinit var notificationViewModelFactory: NotificationViewModel.Factory

    @Inject
    lateinit var sessionManager: SessionManager

    //private val homeViewModel: HomeViewModel by viewModel()
    private val trackingViewModel: TrackingViewModel by viewModel()

    private lateinit var sharedActionViewModel: TestViewModel

    @Inject
    lateinit var localHelper: LocalHelper

    @Inject
    lateinit var userPreferences: UserPreferences


    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var toolbar: Toolbar
    lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TrackingApplication).trackingComponent.inject(this)
        super.onCreate(savedInstanceState)

        
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val receivedUser = intent.getSerializableExtra(EXTRA_USER) as User?

        if (receivedUser == null) {
            securityViewModel.handle(SecurityViewAction.GetUserCurrent)
        }
        setContentView(views.root)
        setupToolbar()
        setupBottomNavigation()

        trackingViewModel.subscribe(this) {
            if (it.isLoadding()) {
                views.waitingView.visibility = View.VISIBLE
            } else
                views.waitingView.visibility = View.GONE
        }


        securityViewModel.subscribe(this) {
            handleStateChange(it)
        }

        profileViewModel.observeViewEvents {
            if (it != null) {
                handleChangeLanguage(it)
            }
        }
//        val fragmentId = intent.getIntExtra("fragmentId", -1)
//        if (fragmentId != -1) {
//            navigateTo(fragmentId)
//        }
    }

    private fun handleChangeLanguage(profileViewEvent: ProfileViewEvent) {
        when (profileViewEvent) {
            is ProfileViewEvent.ResetLanguege -> {
                val refresh = Intent(this, MainActivity::class.java)
//                refresh.putExtra(
//                    "fragmentId",
//                    R.id.nav_profileFragment
//                ) // Đặt ID của Fragment bạn muốn mở
                finish()
                startActivity(refresh)

            }
        }
    }


    private fun handleStateChange(it: SecurityViewState) {
        when (it.userCurrent) {
            is Success -> {
                it.userCurrent.invoke().let { user ->
                    val role = user?.roles?.last()?.authority.toString()
                    securityViewModel.handle(SecurityViewAction.SaveRole(role))
                    securityViewModel.handle(SecurityViewAction.SaveFullName(user?.displayName.toString()))
                }
            }

            is Fail -> {
                Toast.makeText(
                    this,
                    getString(checkStatusApiRes(it.userCurrent as Fail<User>)),
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {}
        }
    }

    override fun getBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    private fun setupToolbar() {
        toolbar = views.toolbar
        toolbar.title = ""
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    @SuppressLint("ResourceType")
    private fun setupBottomNavigation() {
        bottomNavigationView = views.bottomNav
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            views.toolbarTitle.text = destination.label
            when (destination.id) {
                R.id.nav_notificationFragment, R.id.nav_informationFragment, R.id.nav_nextUpdateFragment -> {
                    // Ẩn thanh điều hướng khi chuyển đến Fragment notificationFragment
                    supportActionBar?.show()
                    bottomNavigationView.visibility = View.GONE
                }

                else -> {
                    supportActionBar?.show()
                    bottomNavigationView.visibility = View.VISIBLE
                }
            }
        }
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_timeSheetFragment,
                R.id.nav_trackingFragment,
                R.id.nav_usersFragment,
                R.id.nav_profileFragment
            )
        )
        bottomNavigationView.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun navigateTo(fragmentId: Int) {
        navController.navigate(fragmentId)
    }

    override fun create(initialState: ProfileViewState): ProfileViewModel {
        return profileViewModelFactory.create(initialState)
    }

    override fun create(initialState: InformationViewState): InformationViewModel {
        return informationViewModelFactory.create(initialState)
    }

    override fun create(initialState: UserViewState): UsersViewModel {
        return usersviewmodelFactory.create(initialState)
    }

    override fun create(initialState: TimeSheetViewState): TimeSheetViewModel {
        return timeSheetViewModelFactory.create(initialState)
    }

    override fun create(initialState: TrackingViewState): TrackingViewModel {
        return trackingViewModelFactory.create(initialState)
    }

    override fun create(initialState: SecurityViewState): SecurityViewModel {
        return securityViewModelFactory.create(initialState)
    }

    override fun create(initialState: NotificationViewState): NotificationViewModel {
        return notificationViewModelFactory.create(initialState)
    }

}