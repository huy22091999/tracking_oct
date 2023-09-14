package com.oceantech.tracking.ui

import android.annotation.SuppressLint
import android.app.*
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.viewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.oceantech.tracking.TrackingApplication
import com.oceantech.tracking.core.TrackingBaseActivity
import com.oceantech.tracking.ui.home.HomeViewState
import com.oceantech.tracking.ui.home.HomeViewModel
import com.oceantech.tracking.utils.LocalHelper
import com.oceantech.tracking.databinding.ActivityMainBinding
import java.util.*
import javax.inject.Inject
import com.oceantech.tracking.R
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.ui.home.TestViewModel
import com.oceantech.tracking.ui.information.InformationViewModel
import com.oceantech.tracking.ui.information.InformationViewState
import com.oceantech.tracking.ui.profile.ProfileViewModel
import com.oceantech.tracking.ui.profile.ProfileViewState
import com.oceantech.tracking.ui.users.UserViewState
import com.oceantech.tracking.ui.users.UsersViewModel
import com.oceantech.tracking.ui.users.UsersViewModel_Factory

class MainActivity : TrackingBaseActivity<ActivityMainBinding>(), HomeViewModel.Factory,
    ProfileViewModel.Factory, InformationViewModel.Factory, UsersViewModel.Factory {
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "nimpe_channel_id"
    }

    @Inject
    lateinit var profileViewModelFactory: ProfileViewModel.Factory

    @Inject
    lateinit var informationViewModelFactory: InformationViewModel.Factory

    @Inject
    lateinit var usersviewmodelFactory: UsersViewModel.Factory

    @Inject
    lateinit var sessionManager: SessionManager
    private val homeViewModel: HomeViewModel by viewModel()

    private lateinit var sharedActionViewModel: TestViewModel

    @Inject
    lateinit var localHelper: LocalHelper

    @Inject
    lateinit var homeViewModelFactory: HomeViewModel.Factory


    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var toolbar: Toolbar
    lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TrackingApplication).trackingComponent.inject(this)
        super.onCreate(savedInstanceState)
        sharedActionViewModel = viewModelProvider.get(TestViewModel::class.java)
        setContentView(views.root)
        setupToolbar()
        setupBottomNavigation()
        sharedActionViewModel.test()

        homeViewModel.subscribe(this) {
            if (it.isLoadding()) {
                views.waitingView.visibility = View.VISIBLE
            } else
                views.waitingView.visibility = View.GONE
        }
    }


    override fun create(initialState: HomeViewState): HomeViewModel {
        return homeViewModelFactory.create(initialState)
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
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_HomeFragment,
                R.id.nav_timeSheetFragment,
                R.id.nav_trackingFragment,
                R.id.nav_usersFragment,
                R.id.nav_profileFragment
            )
        )
        bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            views.toolbarTitle.text = destination.label
        }
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


}

