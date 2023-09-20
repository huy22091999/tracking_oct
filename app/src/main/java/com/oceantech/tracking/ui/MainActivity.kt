package com.oceantech.tracking.ui

import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.annotation.MenuRes
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.airbnb.mvrx.viewModel
import com.oceantech.tracking.R
import com.oceantech.tracking.TrackingApplication
import com.oceantech.tracking.core.TrackingBaseActivity
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.databinding.ActivityMainBinding
import com.oceantech.tracking.ui.home.HomeViewEvent
import com.oceantech.tracking.ui.home.HomeViewModel
import com.oceantech.tracking.ui.home.HomeViewState
import com.oceantech.tracking.ui.home.TestViewModel
import com.oceantech.tracking.ui.tracking.TrackingSubViewModel
import com.oceantech.tracking.ui.tracking.TrackingViewState
import com.oceantech.tracking.ui.users.UserViewModel
import com.oceantech.tracking.ui.users.UserViewState
import com.oceantech.tracking.utils.LocalHelper
import com.oceantech.tracking.utils.changeDarkMode
import com.oceantech.tracking.utils.changeLanguage
import com.oceantech.tracking.utils.handleLogOut
import nl.joery.animatedbottombar.AnimatedBottomBar
import javax.inject.Inject
class MainActivity : TrackingBaseActivity<ActivityMainBinding>(), HomeViewModel.Factory,
    UserViewModel.Factory, TrackingSubViewModel.Factory {
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "nimpe_channel_id"
    }
    //viewModel
    private val homeViewModel: HomeViewModel by viewModel()
    private lateinit var sharedActionViewModel: TestViewModel
    //dependency
    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var localHelper: LocalHelper
    @Inject
    lateinit var homeViewModelFactory: HomeViewModel.Factory
    @Inject
    lateinit var userViewModelFactory: UserViewModel.Factory
    @Inject
    lateinit var trackingViewModelFactory: TrackingSubViewModel.Factory
    //view
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var toolbar: Toolbar
    private lateinit var bottomNavigationBar: AnimatedBottomBar


    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TrackingApplication).trackingComponent.inject(this)
        super.onCreate(savedInstanceState)
        sharedActionViewModel = viewModelProvider.get(TestViewModel::class.java)
        setContentView(views.root)
        setupToolbar()
        setupBottomNavigation()
        sharedActionViewModel.test()
        homeViewModel.subscribe(this) {
            if (it.isLoading()) {
                views.contentMain.waitingView.visibility = View.VISIBLE
            } else
                views.contentMain.waitingView.visibility = View.GONE
        }
        homeViewModel.observeViewEvents {
            handleEvent(it)
        }
        changeDarkMode(sessionManager.getDarkMode())
        changeLanguage(sessionManager.getLanguage())
    }
    private fun setupToolbar() {
        toolbar = views.toolbar
        views.title.text = getString(R.string.logo_app)
        setSupportActionBar(toolbar)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }
    private fun setupBottomNavigation() {
        navController = findNavController(R.id.nav_host_fragment_content_main)
        bottomNavigationBar = views.contentMain.bottomNavigationView
        bottomNavigationBar.setOnTabSelectListener(object : AnimatedBottomBar.OnTabSelectListener {
            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ) {
                when (newTab.id) {
                    R.id.nav_HomeFragment -> navController.navigate(R.id.nav_HomeFragment)
                    R.id.nav_usersFragment -> navController.navigate(R.id.nav_usersFragment)
                    R.id.nav_trackingFragment -> navController.navigate(R.id.nav_trackingFragment)
                    R.id.nav_fofileFragment -> navController.navigate(R.id.nav_fofileFragment)
                }
            }

        })
    }
    private fun handleEvent(event: HomeViewEvent?) {
        when (event) {
            is HomeViewEvent.Logout -> handleLogOut()
            is HomeViewEvent.ChangeDarkMode -> handleDarkMode(event.isCheckedDarkMode)
        }
    }
    private fun handleDarkMode(checkedDarkMode: Boolean) {
        sessionManager.saveDarkMode(checkedDarkMode)
        changeDarkMode(checkedDarkMode)
        changeLanguage(sessionManager.getLanguage())
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
            handleLangOnClick("en")
            popup.dismiss()
        }
        view.findViewById<LinearLayout>(R.id.to_lang_vi).setOnClickListener {
            handleLangOnClick("vi")
            popup.dismiss()
        }
        view.findViewById<LinearLayout>(R.id.to_lang_zh).setOnClickListener {
            handleLangOnClick("zh")
            popup.dismiss()
        }
        view.findViewById<LinearLayout>(R.id.to_lang_ko).setOnClickListener {
            handleLangOnClick("ko")
            popup.dismiss()
        }
        view.findViewById<LinearLayout>(R.id.to_lang_th).setOnClickListener {
            handleLangOnClick("th")
            popup.dismiss()
        }
    }
    private fun handleLangOnClick(lang: String){
        changeLanguage(lang)
        sessionManager.saveLanguage(lang)
        recreate()
    }
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_lang, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_change_langue -> {
                showMenu(findViewById(R.id.nav_change_langue), R.menu.menu_main)
                return true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun getBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun create(initialState: HomeViewState): HomeViewModel {
        return homeViewModelFactory.create(initialState)
    }
    override fun create(initialState: UserViewState): UserViewModel {
        return userViewModelFactory.create(initialState)
    }
    override fun create(initialState: TrackingViewState): TrackingSubViewModel {
        return trackingViewModelFactory.create(initialState)
    }

}

