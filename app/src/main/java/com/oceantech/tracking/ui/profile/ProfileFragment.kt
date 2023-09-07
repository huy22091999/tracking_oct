package com.oceantech.tracking.ui.profile

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import android.widget.Toolbar
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModel
import androidx.navigation.ui.AppBarConfiguration
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.oceantech.tracking.R
import com.oceantech.tracking.TrackingApplication
import com.oceantech.tracking.core.TrackingBaseFragment

import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.data.network.UserApi
import com.oceantech.tracking.databinding.FragmentProfileBinding
import com.oceantech.tracking.ui.home.HomeViewAction
import com.oceantech.tracking.ui.security.LoginActivity
import com.oceantech.tracking.utils.checkStatusApiRes
import retrofit2.Call
import retrofit2.Response
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

class ProfileFragment @Inject constructor() :
    TrackingBaseFragment<FragmentProfileBinding>() {
    var user: User? = null
    var isEdit = false
    var language: Int = 1

    @Inject
    lateinit var sessionManager: SessionManager
    lateinit var bottomNavigationView: BottomNavigationView
    private val profileViewModel: ProfileViewModel by activityViewModel()

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileBinding {
        return FragmentProfileBinding.inflate(inflater, container, false);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as TrackingApplication).trackingComponent.inject(
            this
        )
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
        profileViewModel.handle(ProfileViewAction.GetCurrentUser)
        profileViewModel.observeViewEvents {
        }
    }


    private fun showMenu(v: View, @MenuRes menuRes: Int) {
        val inflater =
            requireContext().getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_window, null)
        val popup = PopupWindow(
            view,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        popup.elevation = 20F
        //val drawable=ContextCompat.getDrawable(requireContext(),R.drawable.backgound_box)
        popup.setBackgroundDrawable(getDrawable(requireContext(), R.drawable.backgound_box))
        popup.showAsDropDown(v, 280, -140, Gravity.CENTER_HORIZONTAL)
        view.findViewById<LinearLayout>(R.id.to_lang_en).setOnClickListener {
            changeLangue("en")
            profileViewModel.language = 0
            popup.dismiss()
            profileViewModel.handle(ProfileViewAction.ResetLang)
        }
        view.findViewById<LinearLayout>(R.id.to_lang_vi).setOnClickListener {
            changeLangue("vi")
            profileViewModel.language = 1
            popup.dismiss()
            profileViewModel.handle(ProfileViewAction.ResetLang)
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
        bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)

        val menu: Menu = bottomNavigationView.menu
        menu.findItem(R.id.nav_HomeFragment).title = getString(R.string.menu_home)
        menu.findItem(R.id.nav_trackingFragment).title = getString(R.string.menu_tracking)
        menu.findItem(R.id.nav_usersFragment).title = getString(R.string.menu_users)
        menu.findItem(R.id.nav_timeSheetFragment).title = getString(R.string.menu_time_sheet)
        menu.findItem(R.id.nav_profileFragment).title = getString(R.string.menu_profile)

        views.setting.titleInfo.text = getString(R.string.information)
        views.setting.titleExit.text = getString(R.string.exit)
        views.setting.titleForget.text = getString(R.string.forget_password)
        views.setting.titleLogout.text = getString(R.string.logout)
        views.setting.titleLang.text = getString(R.string.language)

        //menu.findItem(R.id.exit).title = getString(R.string.exit)
        //menu.findItem(R.id.logout).title = getString(R.string.logout)
//        menu.findItem(R.id.nav_change_langue).title =
//            if (lang == "en") getString(R.string.en) else getString(R.string.vi)
    }

    override fun invalidate() = withState(profileViewModel){
        when(it.userCurrent){
            is Success ->{
                user = it.userCurrent.invoke()
                Timber.e("UsersFragment Success: $user")
                Glide.with(requireContext()).load("user.link").placeholder(R.drawable.ic_person).into(views.imageUser)
                views.nameUser.text = user?.displayName
                views.emailUser.text = user?.email

                if (isEdit) {
                    isEdit = false
                    //handleEditUser()
                }
                Toast.makeText(requireContext(), getString(R.string.success), Toast.LENGTH_SHORT).show()
            }
            is Fail -> {
                Toast.makeText(requireContext(), getString(checkStatusApiRes(it.userCurrent)), Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }


}