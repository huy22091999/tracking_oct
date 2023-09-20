package com.oceantech.tracking.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.location.Location
import android.os.Build
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.airbnb.mvrx.Fail
import com.google.android.material.textfield.TextInputEditText
import com.oceantech.tracking.R
import com.oceantech.tracking.ui.profile.InfoActivity
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
    option: ((FragmentTransaction) -> Unit)? = null) {
    supportFragmentManager.commitTransaction(allowStateLoss) {
        option?.invoke(this)
        setCustomAnimations(
            R.anim.enter_from_right,
            R.anim.exit_to_left,
            R.anim.enter_from_left,
            R.anim.exit_to_right )
        replace(frameId, fragmentClass,null, tag).addToBackStack(tag)
    }
}

fun Activity.startActivityAnim(intent: Intent) {
    startActivity(intent)
    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left,)
}

fun Activity.popActivityAnim() {
    finish()
    overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right,)
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun <V : View> setVisibleView(view : V, isVisible : Boolean){
    view.isVisible = isVisible
}

fun <V : View> replaceViewVisible(viewGone : V, viewVisible : V){
    viewGone.isVisible = false
    viewVisible.isVisible = true
}

fun checkTILNull(res : Resources , edt : TextInputEditText): Boolean{
    if (edt.text.toString().trim() == ""){
        edt.error = res.getString(R.string.notEmpty)
        return true
    }
    edt.error = null
    return false
}

fun checkEDTNull(res : Resources , edt : EditText): Boolean{
    if (edt.text.toString().trim() == ""){
        edt.error = res.getString(R.string.notEmpty)
        return true
    }
    edt.error = null
    return false
}

fun checkValidEmail(res : Resources , edt : TextInputEditText): Boolean {
    val strEmail = edt.text.toString().trim()
    if (TextUtils.isEmpty(strEmail) or !Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()){
        edt.error =   res.getString(R.string.validateEmail)

        return true
    }
    edt.error = null
    return false
}

fun checkValidEPassword(res : Resources , edt1 : TextInputEditText, edt2 : TextInputEditText): Boolean {
    val str1 = edt1.text.toString().trim()
    val str2 = edt2.text.toString().trim()
    if (str1 == ""|| str2 == "" || str1 != str2){
        edt2.error =   res.getString(R.string.validatePassword)
        return true
    }
    edt2.error = null
    return false
}

fun<T> checkStatusApiRes(err: Fail<T>): Int {
    return when(err.error.message!!.trim()){
        "HTTP 200" ->{
            R.string.http200
        }
        "HTTP 401" ->{
            R.string.http401
        }
        "HTTP 403" ->{
            R.string.http403
        }
        "HTTP 404" ->{
            R.string.http404
        }
        "HTTP 500" ->{
            R.string.http500
        }
        else -> {
            R.string.http500
        }
    }
}

fun changeMode(isChecked: Boolean?) {
    if (isChecked == true) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    } else {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}
fun Activity.changeLangue(lang: String?) {
    val res: Resources = resources
    val dm: DisplayMetrics = res.displayMetrics
    val conf: Configuration = res.configuration
    res.updateConfiguration(conf, dm)
    conf.setLocale(Locale(lang ?: "en"))


}
