package com.oceantech.tracking.ui

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
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
import com.oceantech.tracking.ui.home.TestViewModel
import com.oceantech.tracking.ui.timesheets.TimeSheetViewModel
import com.oceantech.tracking.ui.timesheets.TimeSheetViewState
import com.oceantech.tracking.ui.tracking.TrackingViewModel
import com.oceantech.tracking.ui.tracking.TrackingViewState

class MainActivity : TrackingBaseActivity<ActivityMainBinding>(), HomeViewModel.Factory, TrackingViewModel.Factory, TimeSheetViewModel.Factory {
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "nimpe_channel_id"
    }

    private val homeViewModel: HomeViewModel by viewModel()

    private lateinit var sharedActionViewModel: TestViewModel

    @Inject
    lateinit var localHelper: LocalHelper

    @Inject
    lateinit var homeViewModelFactory: HomeViewModel.Factory

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
        sharedActionViewModel = viewModelProvider[TestViewModel::class.java]
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
    }
    override fun create(S: TimeSheetViewState): TimeSheetViewModel {
        return timeSheetViewModelFactory.create(S)
    }

    override fun create(initialState: HomeViewState): HomeViewModel {
        return homeViewModelFactory.create(initialState)
    }

    override fun create(state: TrackingViewState): TrackingViewModel {
        return trackingViewModelFactory.create(state)
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
    private fun setupDrawer() {
        drawerLayout = views.appBarMain.drawerLayout
        navView = views.appBarMain.navView
        navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_HomeFragment,
                R.id.trackingFragment,
                R.id.timeSheetFragment
            ), drawerLayout
        )

        //Set up toolbar(action bar) with navigation
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        views.title.text = supportActionBar?.title

        // settings
        navView.setNavigationItemSelectedListener { menuItem ->

            val handled = NavigationUI.onNavDestinationSelected(menuItem, navController)

            // make title of toolbar as label navigation
            views.title.text = menuItem.title

            when (menuItem.itemId) {
                R.id.exit -> {
                    val homeIntent = Intent(Intent.ACTION_MAIN)
                    homeIntent.addCategory(Intent.CATEGORY_HOME)
                    homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(homeIntent)

                }

                R.id.nav_change_langue -> {
                    showMenu(findViewById(R.id.nav_change_langue), R.menu.menu_main)
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

    }

    private fun changeLangue(lang: String) {
        val res: Resources = resources
        val dm: DisplayMetrics = res.displayMetrics
        val conf: Configuration = res.configuration
        val myLocale = Locale(lang)
        conf.setLocale(myLocale)
        res.updateConfiguration(conf, dm)
        updateLanguge(lang)


    }

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

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    fun navigateTo(fragmentId: Int) {
        navController.navigate(fragmentId)
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

            R.id.menu_list_health -> {

                return true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun updateLanguge(lang: String) {
        val menu: Menu = navView.menu
        menu.findItem(R.id.nav_HomeFragment).title = getString(R.string.menu_user_list)
        menu.findItem(R.id.trackingFragment).title = getString(R.string.fragment_tracking)
        menu.findItem(R.id.timeSheetFragment).title = getString(R.string.time_sheet)
        menu.findItem(R.id.personalFragment).title = getString(R.string.personal_information)
        menu.findItem(R.id.nav_change_langue).title =
            if (lang == "en") getString(R.string.en) else getString(R.string.vi)
    }



}

