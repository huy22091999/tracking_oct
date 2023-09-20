package com.oceantech.tracking.utils

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.oceantech.tracking.R
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.ui.home.HomeViewAction
import com.oceantech.tracking.ui.security.LoginActivity
import com.oceantech.tracking.ui.tracking.TrackingViewAction
import com.oceantech.tracking.ui.users.UserViewAction
import timber.log.Timber
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

fun Location?.toText(): String {
    return if (this != null) {
        "($latitude, $longitude)"
    } else {
        "Unknown location"
    }
}
@RequiresApi(Build.VERSION_CODES.O)
fun Date.format(format: String? = null): String {
    val ld = toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    return ld.format(DateTimeFormatter.ofPattern(format ?: "dd/MM/yyyy"))
}
fun AppCompatActivity.addFragment(
    frameId: Int,
    fragment: Fragment,
    allowStateLoss: Boolean = false
) {
    supportFragmentManager.commitTransaction(allowStateLoss) { add(frameId, fragment) }
}
inline fun androidx.fragment.app.FragmentManager.commitTransaction(allowStateLoss: Boolean = false, func: FragmentTransaction.() -> FragmentTransaction) {
    val transaction = beginTransaction().func()
    if (allowStateLoss) {
        transaction.commitAllowingStateLoss()
    } else {
        transaction.commit()
    }
}
fun <T : Fragment> AppCompatActivity.addFragmentToBackstack(
    frameId: Int,
    fragmentClass: Class<T>,
    tag: String? = null,
    allowStateLoss: Boolean = false,
    option: ((FragmentTransaction) -> Unit)? = null,
    bundle: Bundle?=null
    ) {
    supportFragmentManager.
        commitTransaction(allowStateLoss) {
        option?.invoke(this)
        replace(frameId, fragmentClass,bundle, tag).addToBackStack(tag)
    }
}

fun Activity.handleLogOut() {
    SessionManager(applicationContext).also {
        it.deleteAuthToken()
    }

    val intent = Intent(this, LoginActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    startActivity(intent)
    finish()
}

fun Activity.changeLanguage(lang: String) {
    val res: Resources = resources
    val dm: DisplayMetrics = res.displayMetrics
    val conf: Configuration = res.configuration
    val myLocale = Locale(lang)
    conf.setLocale(myLocale)
    res.updateConfiguration(conf, dm)
}

fun Activity.changeDarkMode(isDarkMode: Boolean) {
    AppCompatDelegate.setDefaultNightMode(
        if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
    )
}

fun isNumeric(input: String): Boolean {
    return input.toIntOrNull() != null
}




