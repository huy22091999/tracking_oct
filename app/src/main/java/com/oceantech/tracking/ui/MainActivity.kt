package com.oceantech.tracking.ui

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.annotation.MenuRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.airbnb.mvrx.viewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.oceantech.tracking.TrackingApplication
import com.oceantech.tracking.core.TrackingBaseActivity
import com.oceantech.tracking.ui.home.HomeViewAction
import com.oceantech.tracking.ui.home.HomeViewState
import com.oceantech.tracking.ui.home.HomeViewModel
import com.oceantech.tracking.utils.LocalHelper
import com.google.android.material.navigation.NavigationView
import com.oceantech.tracking.databinding.ActivityMainBinding
import java.util.*
import javax.inject.Inject

import com.oceantech.tracking.R
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.ui.home.TestViewModel
import com.oceantech.tracking.ui.profile.ProfileViewModel
import com.oceantech.tracking.ui.profile.ProfileViewState
import com.oceantech.tracking.ui.security.LoginActivity

class MainActivity : TrackingBaseActivity<ActivityMainBinding>(), HomeViewModel.Factory, ProfileViewModel.Factory {
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "nimpe_channel_id"
    }
    @Inject
    lateinit var profileViewModelFactory: ProfileViewModel.Factory

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
        setSupportActionBar(toolbar)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    @SuppressLint("ResourceType")
    private fun setupBottomNavigation() {
        bottomNavigationView = views.bottomNav
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            navController.graph
        )

        bottomNavigationView.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)
        //toolbar.setupWithNavController(navController, appBarConfiguration)
//        navController.addOnDestinationChangedListener { _, destination, _ ->
//            views.title.text = destination.label
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


}

