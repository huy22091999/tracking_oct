package com.oceantech.tracking.ui

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.MenuRes
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.airbnb.mvrx.viewModel
import com.oceantech.tracking.core.TrackingBaseActivity
import com.oceantech.tracking.ui.home.HomeViewState
import com.oceantech.tracking.ui.home.HomeViewModel
import com.oceantech.tracking.utils.LocalHelper
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.google.firebase.messaging.FirebaseMessaging
import com.oceantech.tracking.databinding.ActivityMainBinding
import java.util.*
import javax.inject.Inject

import com.oceantech.tracking.R
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.ui.home.HomeViewAction
import com.oceantech.tracking.ui.home.TestViewModel
import com.oceantech.tracking.ui.home.user.ModifyUserFragment
import com.oceantech.tracking.ui.information.InfoViewModel
import com.oceantech.tracking.ui.information.InfoViewState
import com.oceantech.tracking.ui.notifications.NotificationViewModel
import com.oceantech.tracking.ui.notifications.NotificationViewState
import com.oceantech.tracking.ui.personal.ModifyPersonalFragment
import com.oceantech.tracking.ui.security.SecurityViewAction
import com.oceantech.tracking.ui.timesheets.TimeSheetViewModel
import com.oceantech.tracking.ui.timesheets.TimeSheetViewState
import com.oceantech.tracking.ui.tracking.TrackingViewModel
import com.oceantech.tracking.ui.tracking.TrackingViewState
import com.oceantech.tracking.utils.DarkModeUtils
import com.oceantech.tracking.utils.NavigationFragment
import com.oceantech.tracking.utils.TrackingContextWrapper
import com.oceantech.tracking.utils.changeDarkMode
import com.oceantech.tracking.utils.changeLanguage
import com.oceantech.tracking.utils.createNotification
import com.oceantech.tracking.utils.handleLogOut
import com.oceantech.tracking.utils.handleNavigationBack
import com.oceantech.tracking.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class MainActivity : TrackingBaseActivity<ActivityMainBinding>(), HomeViewModel.Factory,
    TrackingViewModel.Factory, TimeSheetViewModel.Factory, InfoViewModel.Factory,
    NotificationViewModel.Factory {

    private val homeViewModel: HomeViewModel by viewModel()

    private val testViewModel: TestViewModel by viewModels {
        SavedStateViewModelFactory(application, this)
    }


    @Inject
    lateinit var localHelper: LocalHelper

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var homeViewModelFactory: HomeViewModel.Factory

    @Inject
    lateinit var trackingViewModelFactory: TrackingViewModel.Factory

    @Inject
    lateinit var timeSheetViewModelFactory: TimeSheetViewModel.Factory

    @Inject
    lateinit var mInfoViewModelFactory: InfoViewModel.Factory

    @Inject
    lateinit var notificationViewModelFactory: NotificationViewModel.Factory

    // Create update manager
    private lateinit var appUpdateManager: AppUpdateManager
    private val updateType = AppUpdateType.IMMEDIATE

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(views.root)
        sessionManager.getDarkMode().let {
            DarkModeUtils.isDarkMode = it
            changeDarkMode(it)
        }

        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)

        //Register install listener for flexible update for update manager, just for flexible update
        if (updateType == AppUpdateType.FLEXIBLE) {
            appUpdateManager.registerListener(installStatusListener)
        }

        checkForUpdate() // check update with update type is immediate update
        setupToolbar()
        setupDrawer()
        testViewModel.test()

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task->
            val result = task.result
            homeViewModel.handle(HomeViewAction.GetDevice(result))
        }
        homeViewModel.onEach {
            if (it.isLoading()) {
                views.appBarMain.contentMain.waitingView.visibility = View.VISIBLE
            } else
                views.appBarMain.contentMain.waitingView.visibility = View.GONE
        }

    }

    override fun create(state: NotificationViewState): NotificationViewModel {
        return notificationViewModelFactory.create(state)
    }

    override fun create(state: InfoViewState): InfoViewModel {
        return mInfoViewModelFactory.create(state)
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
        toolbar.setContentInsetsAbsolute(
            0,
            toolbar.contentInsetStartWithNavigation
        ) // make title center of toolbar
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
                R.id.timeSheetFragment,
                R.id.personalFragment,
                R.id.notificationFragment,
                R.id.publicFragment
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
                    finish()
                }

                R.id.nav_change_langue -> {
                    showMenu(findViewById(R.id.nav_change_langue), R.menu.menu_main)
                }

                R.id.log_out -> {
                    handleLogOut()
                }

                R.id.dark_mode -> {
                    handleDarkMode(menuItem)
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
            menuItem.title = getString(R.string.en)

        } else {
            menuItem.title = getString(R.string.vi)
        }
        val buttonShowMenu = actionView as AppCompatImageView
        buttonShowMenu.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_drop))
        buttonShowMenu.setOnClickListener {
            showMenu(findViewById(R.id.nav_change_langue), R.menu.menu_main)
        }

        menu.findItem(R.id.dark_mode).let { item ->
            (item.actionView as SwitchCompat).apply {
                setOnClickListener {
                    handleDarkMode(item)
                }
                isChecked = DarkModeUtils.isDarkMode
            }
        }
    }

    @SuppressLint("InflateParams")
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
            changeLanguage(sessionManager, "en")
        }
        view.findViewById<LinearLayout>(R.id.to_lang_vi).setOnClickListener {
            changeLanguage(sessionManager, "vi")

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
                handleNavigationBack(navController, drawerLayout)
                return true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

//    private fun setNavigationFragment(id: Int): NavigationFragment? {
//        return if (id == R.id.modifyPersonalFragment || id == R.id.modifyUserFragment) {
//            supportFragmentManager.findFragmentById(id) as? NavigationFragment
//        } else {
//            null
//        }
//    }

    private fun handleDarkMode(menuItem: MenuItem) {
        DarkModeUtils.isDarkMode = !DarkModeUtils.isDarkMode
        (menuItem.actionView!! as SwitchCompat).apply {
            isChecked = DarkModeUtils.isDarkMode
        }
        changeDarkMode(DarkModeUtils.isDarkMode)
        recreate()
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

    /**
     * Check that the update is not stuck and interrupted by user who quiting app when in update progress.
     * Should execute this check at all entry points into the app.
     * Just use for immediate update.
     */
    override fun onResume() {
        super.onResume()
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            // Check that update is in progress
            if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    activityResultLauncher,
                    AppUpdateOptions.newBuilder(updateType).build()
                )
            }
        }
    }

    // Handle the immediate update
    private fun checkForUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            val updateAvailable = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val updateAllowed = when (updateType) {
                AppUpdateType.IMMEDIATE -> info.isImmediateUpdateAllowed
                AppUpdateType.FLEXIBLE -> info.isFlexibleUpdateAllowed
                else -> false
            }
            if (updateAvailable && updateAllowed) {
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    activityResultLauncher,
                    AppUpdateOptions.newBuilder(updateType).build()
                )
            }
        }
    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            when (it.resultCode) {
                RESULT_OK -> {
                    showToast(this, getString(R.string.update_app_successfully))
                }

                RESULT_CANCELED -> {
                    showToast(this, getString(R.string.cancel_app_update))
                }

                com.google.android.play.core.install.model.ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                    showToast(this, getString(R.string.update_app_failed))
                }
            }
        }

    //Use listener for flexible update
    private val installStatusListener = InstallStateUpdatedListener { state ->
        when (state.installStatus()) {
            InstallStatus.DOWNLOADING -> {
                val downloaded = state.bytesDownloaded()
                val total = state.totalBytesToDownload()
                createNotification(
                    getString(R.string.noti_channel_id),
                    applicationContext,
                    getString(R.string.update),
                    getString(R.string.downloading),
                    R.layout.download_update_layout,
                    downloaded,
                    total
                )
            }

            InstallStatus.DOWNLOADED -> {
                showToast(
                    applicationContext,
                    getString(R.string.restart_app_update)
                )
                lifecycleScope.launch {
                    delay(5.seconds)
                    appUpdateManager.completeUpdate()
                }
            }

            else -> {}
        }
    }

    /**
     * Unregister listener for flexible update
     */
    override fun onDestroy() {
        super.onDestroy()
        if (updateType == AppUpdateType.FLEXIBLE) {
            appUpdateManager.unregisterListener(installStatusListener)
        }
        sessionManager.saveDarkMode(DarkModeUtils.isDarkMode)
    }


}

