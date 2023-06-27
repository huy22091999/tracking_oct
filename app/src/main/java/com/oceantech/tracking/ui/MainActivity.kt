package com.oceantech.tracking.ui

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.activity.viewModels
import androidx.annotation.MenuRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.airbnb.mvrx.viewModel
import com.oceantech.tracking.TrackingApplication
import com.oceantech.tracking.core.TrackingBaseActivity
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
import com.oceantech.tracking.ui.public_config.PublicViewModel
import com.oceantech.tracking.ui.public_config.PublicViewState
import com.oceantech.tracking.ui.security.LoginActivity
import com.oceantech.tracking.ui.timesheets.TimeSheetViewModel
import com.oceantech.tracking.ui.timesheets.TimeSheetViewState
import com.oceantech.tracking.ui.tracking.TrackingViewModel
import com.oceantech.tracking.ui.tracking.TrackingViewState
import com.oceantech.tracking.utils.addFragmentToBackstack
import com.oceantech.tracking.utils.changeLanguage
import com.oceantech.tracking.utils.handleLogOut
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : TrackingBaseActivity<ActivityMainBinding>(), HomeViewModel.Factory,
    TrackingViewModel.Factory, TimeSheetViewModel.Factory, PublicViewModel.Factory {
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "nimpe_channel_id"
    }

    private val homeViewModel: HomeViewModel by viewModel()

    private val testViewModel: TestViewModel by viewModels {
        SavedStateViewModelFactory(application, this)
    }

    @Inject
    lateinit var localHelper: LocalHelper

    @Inject
    lateinit var homeViewModelFactory: HomeViewModel.Factory

    @Inject
    lateinit var trackingViewModelFactory: TrackingViewModel.Factory

    @Inject
    lateinit var timeSheetViewModelFactory: TimeSheetViewModel.Factory

    @Inject
    lateinit var publicViewModelFactory: PublicViewModel.Factory

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(views.root)
        setupToolbar()
        setupDrawer()
        testViewModel.test()
        homeViewModel.onEach{
            if (it.isLoadding()) {
                views.appBarMain.contentMain.waitingView.visibility = View.VISIBLE
            } else
                views.appBarMain.contentMain.waitingView.visibility = View.GONE
        }

    }
    override fun create(state: PublicViewState): PublicViewModel {
        return publicViewModelFactory.create(state)
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

        // make title of toolbar as label navigation
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            views.title.text = destination.label
        }

        // settings
        navView.setNavigationItemSelectedListener { menuItem ->

            val handled = NavigationUI.onNavDestinationSelected(menuItem, navController)

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
                R.id.log_out -> {
                    handleLogOut()
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
        val actionView: View = menuItem.actionView!!
        val res: Resources = resources
        val conf: Configuration = res.configuration
        val local = conf.locale

        // get the language that showing in the display
        val lang = local.getDisplayLanguage(local)
        if (lang == "English") {
//            homeViewModel.language = 0
            menuItem.title = getString(R.string.en)

        } else {
            menuItem.title = getString(R.string.vi)
//            homeViewModel.language = 1
        }
        val buttonShowMenu = actionView as AppCompatImageView
        buttonShowMenu.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_drop))
        buttonShowMenu.setOnClickListener {
            showMenu(findViewById(R.id.nav_change_langue), R.menu.menu_main)
        }

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
            changeLanguage(localHelper,"en")
        }
        view.findViewById<LinearLayout>(R.id.to_lang_vi).setOnClickListener {
            changeLanguage(localHelper,"vi")
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

    private fun navigateTo(fragmentId: Int) {
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

            R.id.menu_list_user -> {
                navigateTo(R.id.nav_HomeFragment)
                return true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
        return super.onOptionsItemSelected(item)
    }




}

