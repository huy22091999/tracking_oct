package com.oceantech.tracking.utils

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.oceantech.tracking.ui.MainActivity
import java.time.Instant
import java.time.LocalDateTime
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


// Convert ISO 8061 to normal date time

fun String.toLocalDate(isoDateTime: String): String {
    val formatter = DateTimeFormatter.ISO_DATE_TIME.parse(isoDateTime)
    val normalFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val instant = Instant.from(formatter)
    val normalDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

    return normalDateTime.format(normalFormat)
}
//Update language of app
fun Activity.changeLanguage(localHelper: LocalHelper, language: String){
    localHelper.setLanguage(baseContext, language)
    val intent = Intent(this, this.javaClass)
    startActivity(intent)
    finish()
}