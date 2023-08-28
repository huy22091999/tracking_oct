package com.oceantech.tracking.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.airbnb.mvrx.viewModel
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
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.ui.infomation.InfoViewModel
import com.oceantech.tracking.ui.infomation.InfoViewsState
import com.oceantech.tracking.ui.home.TestViewModel
import com.oceantech.tracking.ui.security.LoginActivity
import com.oceantech.tracking.ui.timesheet.TimeSheetViewModel
import com.oceantech.tracking.ui.timesheet.TimeSheetViewState
import com.oceantech.tracking.ui.tracking.TrackingViewModel
import com.oceantech.tracking.ui.tracking.TrackingViewState
import com.oceantech.tracking.ui.users.UserViewModel
import com.oceantech.tracking.ui.users.UserViewState
import com.oceantech.tracking.ui.users.UsersFragmentDirections
import com.oceantech.tracking.ui.users.UsersViewEvent
import timber.log.Timber

class MainActivity : TrackingBaseActivity<ActivityMainBinding>(), HomeViewModel.Factory,
    UserViewModel.Factory, InfoViewModel.Factory, TrackingViewModel.Factory,
    TimeSheetViewModel.Factory {
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "nimpe_channel_id"
    }

    private val homeViewModel: HomeViewModel by viewModel()
    private val userViewModel: UserViewModel by viewModel()
    private val infoViewModel: InfoViewModel by viewModel()
    private val trackingViewModel: TrackingViewModel by viewModel()
    private val timeSheetViewModel: TimeSheetViewModel by viewModel()

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

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TrackingApplication).trackingComponent.inject(this)
        super.onCreate(savedInstanceState)
        sharedActionViewModel = viewModelProvider.get(TestViewModel::class.java)
        setContentView(views.root)
        setupToolbar()
        setupDrawer()
        sharedActionViewModel.test()

        homeViewModel.subscribe(this) {
            if (it.isLoadding()) {
                views.appBarMain.contentMain.waitingView.visibility = View.VISIBLE
            } else
                views.appBarMain.contentMain.waitingView.visibility = View.GONE
        }

        userViewModel.observeViewEvents {
            if (it != null) {
                handlEvent(it)
            }

        }

    }

    private fun <T> handlEvent(viewEvent: T) {
        when (viewEvent) {
            is UsersViewEvent.ReturnDetailViewEvent -> {
                navigateTo(idFragment = null, user = viewEvent.user)
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

    @SuppressLint("ResourceType", "LogNotTimber", "UseCompatLoadingForDrawables")
    private fun setupDrawer() {
        drawerLayout = views.appBarMain.drawerLayout
        navView = views.appBarMain.navView
        navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(
            navController.graph,
            drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener{ _, destination, _ ->
            if(destination.id != R.id.nav_HomeFragment) {
                toolbar.title = ""
            }

        }

        // settings
        navView.setNavigationItemSelectedListener { menuItem ->

            val handled = NavigationUI.onNavDestinationSelected(menuItem, navController)
            when (menuItem.itemId) {
                R.id.exit -> {
                    android.os.Process.killProcess(android.os.Process.myPid());
                }

                R.id.nav_change_langue -> {
                    showMenu(findViewById(R.id.nav_change_langue), R.menu.menu_main)
                }

                R.id.logout -> {
                    handleLogout()
                }

                else -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    handled
                }
            }
            handled
        }
        val menu: Menu = navView.menu
        val menuItem = menu.findItem(R.id.nav_change_langue)
        val actionView: View = MenuItemCompat.getActionView(menuItem)
        val res: Resources = resources
        val conf: Configuration = res.configuration
        val local = conf.locale
        val lang = local.displayLanguage
        if (lang == "English") {
            homeViewModel.language = 0
            menuItem.title = getString(R.string.en)

        } else {
            menuItem.title = getString(R.string.vi)
            homeViewModel.language = 1
        }
        var buttonShowMenu = actionView as AppCompatImageView
        buttonShowMenu.setImageDrawable(getDrawable(R.drawable.ic_drop))
        buttonShowMenu.setOnClickListener {
            showMenu(findViewById(R.id.nav_change_langue), R.menu.menu_main)
        }

        // darkmode
        handleMode(
            navView.menu.findItem(R.id.nav_change_mode).actionView as SwitchCompat,
            R.menu.menu_main
        )
    }

    private fun handleLogout() {
        sessionManager.let {
            it.removeTokenRefresh()
            it.removeAuthToken()
        }
        startActivity(Intent(this, LoginActivity::class.java))
        finishAffinity()
    }


    private fun changeLangue(lang: String) {
        val res: Resources = resources
        val dm: DisplayMetrics = res.displayMetrics
        val conf: Configuration = res.configuration
        val myLocale = Locale(lang)
        conf.setLocale(myLocale)
        res.updateConfiguration(conf, dm)

        sessionManager.let { it.saveLanguage(lang) }
//        updateLanguge(lang)
        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity()
    }

    //menu change language
    private fun showMenu(v: View, @MenuRes menuRes: Int) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_window, null)
        val popup = PopupWindow(
            view,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        popup.elevation = 20F
        popup.setBackgroundDrawable(getDrawable(R.drawable.backgound_box))
        popup.showAsDropDown(v, 280, -140, Gravity.CENTER_HORIZONTAL)
        view.findViewById<LinearLayout>(R.id.to_lang_en).setOnClickListener {
            changeLangue("en")
            homeViewModel.language = 0
            popup.dismiss()
            homeViewModel.handle(HomeViewAction.ResetLang)
        }
        view.findViewById<LinearLayout>(R.id.to_lang_vi).setOnClickListener {
            changeLangue("vi")
            homeViewModel.language = 1
            popup.dismiss()
            homeViewModel.handle(HomeViewAction.ResetLang)
        }
    }


    private fun handleMode(switchMode: SwitchCompat, @MenuRes menuRes: Int) {
        sessionManager.fetchDarkMode().let { switchMode.isChecked = it ?: false }

        switchMode.setTrackResource(R.drawable.track_switch)
        switchMode.setThumbResource(R.drawable.thumb_switch)
        switchMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            sessionManager.let { it.saveDarkMode(isChecked) }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
        return NavigationUI.navigateUp(navController, drawerLayout) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    fun navigateTo(idFragment: String?, user: User?) {
        try {
            if (user != null) {
                val action =
                    UsersFragmentDirections.actionNavUsersFragmentToUserDetailFragment(user)
                navController.navigate(action)
            }
        } catch (e: Exception) {
            Timber.e(e.toString())
            Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_SHORT).show()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (drawerLayout.isOpen) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
                return true
            }

            R.id.nav_usersFragment -> {
                if (drawerLayout.isOpen) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
                return true
            }

            R.id.nav_infoFragment -> {
                if (drawerLayout.isOpen) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
                return true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun updateLanguge(lang: String) {
        val menu: Menu = navView.menu
        menu.findItem(R.id.nav_HomeFragment).title = getString(R.string.menu_home)
        menu.findItem(R.id.nav_usersFragment).title = getString(R.string.menu_users)
        menu.findItem(R.id.nav_change_langue).title =
            if (lang == "en") getString(R.string.en) else getString(R.string.vi)
        menu.findItem(R.id.exit).title = getString(R.string.exit)
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

