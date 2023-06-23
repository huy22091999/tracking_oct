package com.oceantech.tracking.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.ui.MainActivity
import com.oceantech.tracking.ui.item_decoration.ItemDecoration
import com.oceantech.tracking.ui.security.LoginActivity
import com.oceantech.tracking.ui.timesheets.adapter.TimeSheetAdapter
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

fun showToast(context: Context, content: String){
    Toast.makeText(context, content, Toast.LENGTH_SHORT).show()
}
//set up the recycle view
fun <T: RecyclerView.ViewHolder, R: RecyclerView.Adapter<T>> setupRecycleView(rv: RecyclerView, adapter: R, context: Context){
    rv.adapter = adapter
    rv.addItemDecoration(ItemDecoration(20))
    rv.layoutManager = LinearLayoutManager(context)
}

fun Activity.handleLogOut(){
    SessionManager(applicationContext).also {
        it.deleteAuthToken()
    }
    val intent = Intent(this, LoginActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    startActivity(intent)
    finish()
}