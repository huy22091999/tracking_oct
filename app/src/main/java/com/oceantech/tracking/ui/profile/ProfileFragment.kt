package com.oceantech.tracking.ui.profile

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.oceantech.tracking.R
import com.oceantech.tracking.TrackingApplication
import com.oceantech.tracking.core.TrackingBaseFragment

import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.databinding.FragmentProfileBinding
import com.oceantech.tracking.ui.home.HomeViewAction
import com.oceantech.tracking.ui.security.LoginActivity
import java.util.Locale
import javax.inject.Inject

class ProfileFragment : TrackingBaseFragment<FragmentProfileBinding>() {
    var user: User? = null
    var isEdit = false


    @Inject
    lateinit var sessionManager: SessionManager
    lateinit var bottomNavigationView: BottomNavigationView

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileBinding {
        return FragmentProfileBinding.inflate(inflater, container, false);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application.applicationContext as TrackingApplication).trackingComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        views.setting.logout.setOnClickListener {
            sessionManager.let { it.clearAuthToken() }
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        views.setting.exit.setOnClickListener {
            val homeIntent = Intent(Intent.ACTION_MAIN)
            homeIntent.addCategory(Intent.CATEGORY_HOME)
            homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(homeIntent)
        }
        views.setting.language.setOnClickListener {
            showMenu(views.setting.language, R.menu.menu_main)
        }
    }
    private fun showMenu(v: View, @MenuRes menuRes: Int) {
        val inflater = requireContext().getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_window, null)
        val popup = PopupWindow(
            view,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        popup.elevation = 20F
        //val drawable=ContextCompat.getDrawable(requireContext(),R.drawable.backgound_box)
        popup.setBackgroundDrawable(getDrawable(requireContext(),R.drawable.backgound_box))
        popup.showAsDropDown(v, 280, -140, Gravity.CENTER_HORIZONTAL)
        view.findViewById<LinearLayout>(R.id.to_lang_en).setOnClickListener {
            changeLangue("en")
            //homeViewModel.language = 0
            popup.dismiss()
            //homeViewModel.handle(HomeViewAction.ResetLang)
        }
        view.findViewById<LinearLayout>(R.id.to_lang_vi).setOnClickListener {
            changeLangue("vi")
            //homeViewModel.language = 1
            popup.dismiss()
            //homeViewModel.handle(HomeViewAction.ResetLang)
        }
    }
    private fun changeLangue(lang: String) {
        val res: Resources = resources
        val dm: DisplayMetrics = res.displayMetrics
        val conf: Configuration = res.configuration
        val myLocale = Locale(lang)
        conf.setLocale(myLocale)
        res.updateConfiguration(conf, dm)
        sessionManager.let { it.saveLanguage(lang) }
        updateLanguge(lang)


    }
    private fun updateLanguge(lang: String) {
        val menu: Menu = (bottomNavigationView).menu
        menu.findItem(R.id.nav_HomeFragment).title = getString(R.string.menu_home)
        menu.findItem(R.id.nav_trackingFragment).title = getString(R.string.menu_tracking)
        menu.findItem(R.id.nav_usersFragment).title = getString(R.string.menu_users)
        menu.findItem(R.id.nav_timeSheetFragment).title = getString(R.string.menu_time_sheet)
        menu.findItem(R.id.nav_profileFragment).title = getString(R.string.menu_profile)
        menu.findItem(R.id.exit).title = getString(R.string.exit)
        menu.findItem(R.id.logout).title = getString(R.string.logout)
        menu.findItem(R.id.nav_change_langue).title =
            if (lang == "en") getString(R.string.en) else getString(R.string.vi)
    }

}