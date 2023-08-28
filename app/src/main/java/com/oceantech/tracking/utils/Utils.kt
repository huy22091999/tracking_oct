package com.oceantech.tracking.utils

import android.content.res.Resources
import android.location.Location
import android.os.Build
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.view.isVisible
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.airbnb.mvrx.Fail
import com.google.android.material.textfield.TextInputEditText
import com.oceantech.tracking.R
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
        replace(frameId, fragmentClass,null, tag).addToBackStack(tag)
    }
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
fun checkRadioGRNull(radio: RadioGroup): Boolean{
    var radioButton = (radio.getChildAt(radio.size - 1) as AppCompatRadioButton)
    if (radio.checkedRadioButtonId == -1){
        radioButton.setError(R.string.notEmpty.toString())
        return true
    }
    radioButton.setError(null)
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

//private fun changeLangue(lang: String) {
//    val res: Resources = resources
//    val dm: DisplayMetrics = res.displayMetrics
//    val conf: Configuration = res.configuration
//    val myLocale = Locale(lang)
//    conf.setLocale(myLocale)
//    res.updateConfiguration(conf, dm)
//    updateLanguge(lang)
//}

