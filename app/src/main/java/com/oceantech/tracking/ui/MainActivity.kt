package com.oceantech.tracking.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.ui.*
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.viewModel
import com.airbnb.mvrx.withState
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import com.oceantech.tracking.R
import com.oceantech.tracking.TrackingApplication
import com.oceantech.tracking.core.TrackingBaseActivity
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.databinding.ActivityMainBinding
import com.oceantech.tracking.ui.home.*
import com.oceantech.tracking.ui.profile.InfoViewModel
import com.oceantech.tracking.ui.profile.InfoViewsState
import com.oceantech.tracking.ui.security.LoginActivity
import com.oceantech.tracking.ui.timesheet.TimeSheetViewModel
import com.oceantech.tracking.ui.timesheet.TimeSheetViewState
import com.oceantech.tracking.ui.tracking.TrackingViewModel
import com.oceantech.tracking.ui.tracking.TrackingViewState
import com.oceantech.tracking.ui.users.UserViewModel
import com.oceantech.tracking.ui.users.UserViewState
import com.oceantech.tracking.ui.users.UsersFragmentDirections
import com.oceantech.tracking.ui.users.UsersViewEvent
import com.oceantech.tracking.utils.LocalHelper
import com.oceantech.tracking.utils.changeLangue
import com.oceantech.tracking.utils.changeMode
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class MainActivity : TrackingBaseActivity<ActivityMainBinding>(), HomeViewModel.Factory,
    UserViewModel.Factory, InfoViewModel.Factory, TrackingViewModel.Factory,
    TimeSheetViewModel.Factory {
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "nimpe_channel_id"
    }

    private val homeViewModel: HomeViewModel by viewModel()
    private val userViewModel: UserViewModel by viewModel()
    private val trackingViewModel: TrackingViewModel by viewModel()

    private lateinit var sharedActionViewModel: TestViewModel


    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var localHelper: LocalHelper

    @Inject
    lateinit var homeViewModelFactory: HomeViewModel.Factory

    @Inject
    lateinit var userViewModelFactory: UserViewModel.Factory

    @Inject
    lateinit var infoViewModelFactory: InfoViewModel.Factory

    @Inject
    lateinit var trackingViewModelFactory: TrackingViewModel.Factory

    @Inject
    lateinit var timeSheetViewModelFactory: TimeSheetViewModel.Factory

    private lateinit var toolbar: Toolbar
    lateinit var adapterViewPager: HomeViewpagerAdapter
    var positionCurentTab = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TrackingApplication).trackingComponent.inject(this)
        super.onCreate(savedInstanceState)
        sharedActionViewModel = viewModelProvider.get(TestViewModel::class.java)
        homeViewModel.handle(HomeViewAction.GetCurrentUser)

        setContentView(views.root)
        setupToolbar()
        setupTabNav()
        sharedActionViewModel.test()

        subscribeStateViewModel()

        userViewModel.observeViewEvents {
            if (it != null) {
                handlEvent(it)
            }
        }

        homeViewModel.observeViewEvents {
            if (it != null) {
                handlEvent(it)
            }
        }

    }

    @SuppressLint("LogNotTimber")
    private fun subscribeStateViewModel() {
        homeViewModel.subscribe(this) {
            if (it.isLoadding()) {
                views.appBarMain.contentMain.waitingView.visibility = View.VISIBLE
            } else
                views.appBarMain.contentMain.waitingView.visibility = View.GONE

        }

        trackingViewModel.subscribe(this) {
            if (it.isScrollDown) {
                handleHideAppbar(true)
            } else {
                handleHideAppbar(false)
            }
        }
    }

    private fun <T> handlEvent(viewEvent: T) {
        when (viewEvent) {
            is UsersViewEvent.ReturnDetailViewEvent -> {
            }

            is HomeViewEvent.handleSwitchMode -> {
                handleMode(viewEvent.isDarkMode)
            }
            is HomeViewEvent.handleChangeLanguage -> {
                configLangue(viewEvent.language)
            }
            is HomeViewEvent.logoutEvent -> {
                handleLogout()
            }
        }
    }

    override fun getBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    private fun setupToolbar() {
        toolbar = views.toolbar
        toolbar.title = ""
        setSupportActionBar(toolbar)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    @SuppressLint("SuspiciousIndentation")
    private fun setupTabNav() {
        homeViewModel.handle(HomeViewAction.GetItemTablayout)

        adapterViewPager = HomeViewpagerAdapter(supportFragmentManager, lifecycle)
        views.appBarMain.contentMain.viewpager2.adapter = adapterViewPager

        TabLayoutMediator(
            views.appBarMain.navTab,
            views.appBarMain.contentMain.viewpager2
        ) { tab, position ->
            withState(homeViewModel) {
                var listItem = it.itemTabLayout.invoke()
                if (listItem != null)
                    tab.setIcon(listItem[position].unIcon)
                        .setTag(listItem[position].id)
            }
        }.attach()

        views.appBarMain.navTab.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                positionCurentTab = tab?.position ?: 0
                when (tab?.position) {
                    0 -> handleHideAppbar(true)
                    else -> handleHideAppbar(false)
                }

                withState(homeViewModel) {
                    if (it.itemTabLayout.invoke() != null)
                        tab?.setIcon(it.itemTabLayout.invoke()!![tab.position].icon)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                withState(homeViewModel) {
                    if (it.itemTabLayout.invoke() != null)
                        tab?.setIcon(it.itemTabLayout.invoke()!![tab.position].unIcon)
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
    }

    private fun handleLogout() {
        sessionManager.let {
            it.removeTokenRefresh()
            it.removeAuthToken()
        }
        startActivity(Intent(this, LoginActivity::class.java))
        finishAffinity()
    }


    private fun configLangue(lang: String) {
        changeLangue(lang)
        sessionManager.saveLanguage(lang)
        recreate()
    }

    private fun handleHideAppbar(isVisible: Boolean) {
        if (isVisible) {
            views.appbar.animate().translationY(-0F * views.appbar.height)
            views.appBarMain.navTab.animate().translationY(-0F * views.appbar.height)
            views.appBarMain.lineNavTab.animate().translationY(-0F * views.appbar.height)
        } else {
            views.appbar.animate().translationY(-1F * views.appbar.height)
            views.appBarMain.navTab.animate().translationY(-1F * views.appbar.height)
            views.appBarMain.lineNavTab.animate().translationY(-1F * views.appbar.height)
        }
    }

    private fun handleMode(isDarkMode: Boolean) {
        changeMode(isDarkMode)
        sessionManager.let {
            it.saveDarkMode(isDarkMode)
            changeLangue(it.fetchLanguage())
        }
    }
    override fun onBackPressed() {
        if (views.appBarMain.contentMain.viewpager2.currentItem != 0) {
            views.appBarMain.contentMain.viewpager2.setCurrentItem(0, true);
        } else {
            finish();
        }
    }

    override fun create(initialState: HomeViewState): HomeViewModel {
        return homeViewModelFactory.create(initialState)
    }

    override fun create(initialState: UserViewState): UserViewModel {
        return userViewModelFactory.create(initialState)
    }

    override fun create(initialState: InfoViewsState): InfoViewModel {
        return infoViewModelFactory.create(initialState)
    }

    override fun create(initialState: TrackingViewState): TrackingViewModel {
        return trackingViewModelFactory.create(initialState)
    }

    override fun create(initialState: TimeSheetViewState): TimeSheetViewModel {
        return timeSheetViewModelFactory.create(initialState)
    }


}

