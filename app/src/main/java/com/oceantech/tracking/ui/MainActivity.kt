package com.oceantech.tracking.ui

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.viewModel
import com.google.android.material.navigation.NavigationView
import com.oceantech.tracking.R
import com.oceantech.tracking.TrackingApplication
import com.oceantech.tracking.core.TrackingBaseActivity
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.databinding.ActivityMainBinding
import com.oceantech.tracking.ui.home.HomeViewAction
import com.oceantech.tracking.ui.home.HomeViewEvent
import com.oceantech.tracking.ui.home.HomeViewModel
import com.oceantech.tracking.ui.home.HomeViewState
import com.oceantech.tracking.ui.home.TestViewModel
import com.oceantech.tracking.ui.profile.MyProfileFragmentDirections
import com.oceantech.tracking.ui.profile.NextUpdateFragmentDirections
import com.oceantech.tracking.ui.profile.UpdateProfileFragmentDirections
import com.oceantech.tracking.ui.security.LoginActivity
import com.oceantech.tracking.ui.security.SecurityViewState
import com.oceantech.tracking.ui.security.SplashActivity
import com.oceantech.tracking.ui.tracking.TrackingFragmentDirections
import com.oceantech.tracking.ui.user.DetailUserFragmentDirections
import com.oceantech.tracking.ui.user.UserFragmentDirections
import com.oceantech.tracking.utils.LocalHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import javax.inject.Inject

class MainActivity : TrackingBaseActivity<ActivityMainBinding>(), HomeViewModel.Factory, LifecycleOwner {
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "nimpe_channel_id"
    }

    private val homeViewModel: HomeViewModel by viewModel()
    private lateinit var sharedActionViewModel: TestViewModel

    @Inject
    lateinit var localHelper: LocalHelper

    @Inject
    lateinit var homeViewModelFactory: HomeViewModel.Factory

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

        CoroutineScope(Dispatchers.Main).launch {
            homeViewModel.handleTokenDevice(this@MainActivity)
        }

        homeViewModel.observeViewEvents {
            if(it!=null){
                handleEvents(it)
            }
        }

        homeViewModel.subscribe(this) {
            handleStateChange(it)
        }
    }

    private fun handleEvents(viewEvent: HomeViewEvent) {
        when(viewEvent){
            is HomeViewEvent.ReturnUpdateTracking ->{
                views.appBar.visibility = View.GONE
                val direction = TrackingFragmentDirections.actionNavAllTrackingFragmentToNavTrackingFragment(id = viewEvent.id, content = viewEvent.content)
                navController.navigate(direction)
            }

            is HomeViewEvent.ReturnAddTracking -> {
                views.appBar.visibility = View.GONE
                navigateTo(R.id.nav_trackingFragment)
            }

            is HomeViewEvent.ReturnDetailUser-> {
                views.appBar.visibility = View.GONE
                val direction = UserFragmentDirections.actionNavUserFragmentToDetailUserFragment(viewEvent.user,isMyself = false)
                navController.navigate(direction)
            }

            is HomeViewEvent.ReturnTracking -> {
                views.appBar.visibility = View.VISIBLE
                navigateTo(R.id.nav_allTrackingFragment)
            }

            is HomeViewEvent.ReturnListUsers ->{
                views.appBar.visibility = View.VISIBLE
                navigateTo(R.id.nav_userFragment)
            }

            is HomeViewEvent.ReturnEditInfo -> {
                navigateTo(id = null, content = null, user = viewEvent.user, isMyself = false)
            }

            is HomeViewEvent.ReturnUpdateInfo -> {
                navigateTo(id = null, content = null, user = viewEvent.user, isMyself = true)
            }

            is HomeViewEvent.ReturnNextUpdate -> {
                val direction = UpdateProfileFragmentDirections.actionUpdateProfileFragmentToNextUpdateFragment(viewEvent.user, isMyself = viewEvent.isMyself)
                navController.navigate(direction)
            }

            is HomeViewEvent.ReturnProfile -> {
                views.appBar.visibility = View.VISIBLE
                navigateTo(R.id.nav_myProfileFragment)
            }
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
        views.title.text = getString(R.string.tracking)
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
                R.id.nav_allTrackingFragment,
                R.id.nav_trackingFragment,
                R.id.nav_timeSheetFragment,
                R.id.nav_userFragment,
                R.id.nav_detailUserFragment,
                R.id.nav_myProfileFragment
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

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

                R.id.nav_change_theme -> {
                    showMenuTheme(findViewById(R.id.nav_change_theme), R.menu.menu_main)
                }

                R.id.logout -> {
                    homeViewModel.handle(HomeViewAction.Logout)
                }
                else -> {
                    views.title.text = getString(R.string.tracking)
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
        menuItem.title = getString(R.string.language)

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
        val sessionManager = SessionManager(this@MainActivity)
        sessionManager.saveAppLanguage(lang)
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

    @SuppressLint("MissingInflatedId")
    private fun showMenuTheme(v: View, @MenuRes menuRes: Int) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_theme, null)
        val popup = PopupWindow(
            view,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        popup.elevation = 20F
        popup.showAsDropDown(v, 280, -140, Gravity.CENTER_HORIZONTAL)
        view.findViewById<LinearLayout>(R.id.to_light_theme).setOnClickListener {
            setupTheme(AppCompatDelegate.MODE_NIGHT_NO)
            popup.dismiss()
        }
        view.findViewById<LinearLayout>(R.id.to_dark_theme).setOnClickListener {
            setupTheme(AppCompatDelegate.MODE_NIGHT_YES)
            popup.dismiss()
        }
        view.findViewById<LinearLayout>(R.id.to_system_theme).setOnClickListener {
            setupTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            popup.dismiss()
        }
    }

    private fun setupTheme(modeTheme:Int){
        AppCompatDelegate.setDefaultNightMode(modeTheme)
        val sessionManager = SessionManager(this@MainActivity)
        sessionManager.saveAppTheme(modeTheme.toString())
        val i = Intent(this@MainActivity, MainActivity::class.java)
        overridePendingTransition(0, 0)
        startActivity(i)
        overridePendingTransition(0, 0)
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

    private fun navigateTo(fragmentId: Int? = null, id:Int? = null, content:String ? = null, user:User ? = null, isMyself: Boolean? = null) {
        if(id != null){
            val direction = TrackingFragmentDirections.actionNavAllTrackingFragmentToNavTrackingFragment(id, content!!)
            navController.navigate(direction)
        }
        if(user != null && id == null){
            if(isMyself == true){
                if (navController.currentDestination?.id == R.id.nav_myProfileFragment){
                    views.appBar.visibility = View.GONE
                    val direction = MyProfileFragmentDirections.actionNavMyProfileFragmentToUpdateProfileFragment(user,isMyself = true)
                    navController.navigate(direction)
                }
                if(navController.currentDestination?.id == R.id.nav_nextUpdateFragment){
                    views.appBar.visibility = View.GONE
                    val direction = NextUpdateFragmentDirections.actionNavNextUpdateFragmentToNavUpdateProfileFragment(user, isMyself = true)
                    navController.navigate(direction)
                }
            }
            if(isMyself == false){
                if (navController.currentDestination?.id == R.id.nav_detailUserFragment) {
                    val direction = DetailUserFragmentDirections.actionNavDetailUserFragmentToUpdateProfileFragment(user,isMyself = false)
                    navController.navigate(direction)
                } else {
                    val direction = NextUpdateFragmentDirections.actionNavNextUpdateFragmentToNavUpdateProfileFragment(user,isMyself = false)
                    navController.navigate(direction)
                }
            }
        }
        else {
            navController.navigate(fragmentId!!)
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
            else -> {
                views.title.text = getString(R.string.tracking)
                super.onOptionsItemSelected(item)
            }
        }
        views.title.text = getString(R.string.tracking)
        return super.onOptionsItemSelected(item)
    }

    private fun showBottomDialog(){
        val dialog = Dialog(this@MainActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottom_sheet_tracking_layout)

        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM);
    }

    private fun updateLanguge(lang: String) {
        val menu: Menu = navView.menu
        menu.findItem(R.id.nav_userFragment).title = getString(R.string.users)
        menu.findItem(R.id.nav_allTrackingFragment).title = getString(R.string.menu_tracking)
        menu.findItem(R.id.nav_timeSheetFragment).title = getString(R.string.time_sheet)
        menu.findItem(R.id.nav_change_theme).title = getString(R.string.theme)
        menu.findItem(R.id.logout).title = getString(R.string.logout)
        menu.findItem(R.id.nav_change_langue).title = getString(R.string.language)
        //    if (lang == "en") getString(R.string.en) else getString(R.string.vi)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        //visible appBar
        if (navController.currentDestination?.id == R.id.nav_allTrackingFragment
            || navController.currentDestination?.id == R.id.nav_timeSheetFragment
            || navController.currentDestination?.id == R.id.nav_myProfileFragment
            || navController.currentDestination?.id == R.id.nav_userFragment) {
            views.appBar.visibility = View.VISIBLE
            views.title.text = getString(R.string.tracking)
        }
    }

    private fun handleStateChange(it: HomeViewState){
        when(it.asyncLogout){
            is Success -> {
                val sessionManager = SessionManager(this@MainActivity)
                sessionManager.clearAuthToken()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finishAffinity()
                homeViewModel.removeStateLogout()
            }
            is Fail -> {

            }
        }
    }
}

