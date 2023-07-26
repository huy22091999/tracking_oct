package com.oceantech.tracking.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.location.Location
import android.os.Build
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RemoteViews
import android.widget.Toast
import androidx.activity.addCallback
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.messaging.FirebaseMessaging
import com.oceantech.tracking.R
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.ui.security.LoginActivity
import timber.log.Timber
import java.time.Instant
import java.time.LocalDate
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

inline fun androidx.fragment.app.FragmentManager.commitTransaction(
    allowStateLoss: Boolean = false,
    func: FragmentTransaction.() -> FragmentTransaction
) {
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
    option: ((FragmentTransaction) -> Unit)? = null
) {
    supportFragmentManager.commitTransaction(allowStateLoss) {
        option?.invoke(this)
        replace(frameId, fragmentClass, null, tag).addToBackStack(tag)
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

// Convert normal date time to ISO Instant
fun toIsoInstant(localDate: String): String {
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy" )
    val date = LocalDate.parse(localDate, formatter)
    val zonedDateTime = date.atStartOfDay(ZoneId.systemDefault())
    return zonedDateTime.toInstant().toString()
}



/**
 * Update language of app.
 * Starting activity will attach new base context having configuration of language that we choose
 */
fun AppCompatActivity.changeLanguage(sessionManager: SessionManager, language: String) {
    sessionManager.saveLanguage(language)
    val intent = Intent(this, this.javaClass)
    startActivity(intent)
    finish()
}

fun showToast(context: Context, content: String) {
    Toast.makeText(context, content, Toast.LENGTH_SHORT).show()
}

//set up the recycle view
fun <T : RecyclerView.ViewHolder, R : RecyclerView.Adapter<T>> setupRecycleView(
    rv: RecyclerView,
    adapter: R,
    context: Context,
    distance: Int = 20
) {
    rv.adapter = adapter
    rv.addItemDecoration(ItemDecoration(distance))
    rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

}


fun Activity.handleLogOut() {
    SessionManager(applicationContext).also {
        it.deleteAuthToken()
    }
    FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener {
        Timber.tag("LogOut").i("Token deleted")
    }

    val intent = Intent(this, LoginActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    startActivity(intent)
    finish()
}

fun createNotification(
    id: String,
    context: Context,
    contentTitle: String,
    contentText: String,
    @LayoutRes layoutId: Int = 0,
    progress: Long = 0,
    max: Long = 0,
    pendingIntent: PendingIntent? = null,
    @DrawableRes iconId: Int = 0
): Notification {
    var views: RemoteViews? = null
    if (layoutId != 0) {
        views = RemoteViews(context.packageName, layoutId)
        if (progress > 0 && max > 0) views.setProgressBar(
            R.id.downloadPB,
            max.toInt(),
            progress.toInt(),
            false
        )
    }

    return NotificationCompat.Builder(context, id)
        .setContentTitle(contentTitle)
        .setSmallIcon(iconId)
        .setContentText(contentText)
        .setStyle(NotificationCompat.DecoratedCustomViewStyle())
        .setCustomContentView(views)
        .setContentIntent(pendingIntent)
        .build()
}

@SuppressLint("LogNotTimber")
fun Fragment.checkError(errorCode: String) {
    val context = this.requireContext()
    val name = this::class.java.simpleName
    when (errorCode) {
        "HTTP 500 " -> {
            showToast(context, context.getString(R.string.server_error_message))
            Log.i(name, errorCode)
        }

        "HTTP 400 " -> {
            showToast(context, getString(R.string.input_error_message))
            Log.i(name, errorCode)
        }

        "HTTP 404 " -> {
            showToast(context, getString(R.string.request_error_message))
            Log.i(name, errorCode)
        }

        "HTTP 403 " -> {
            showToast(context, getString(R.string.forbidden_error_message))
            Log.i(name, errorCode)
        }

        "HTTP 401 " -> {
            showToast(context, getString(R.string.authorization_error_message))
            Log.i(name, errorCode)
        }
    }
}

object DarkModeUtils {
    var isDarkMode: Boolean = false
}

fun changeDarkMode(isDarkMode: Boolean) {
    AppCompatDelegate.setDefaultNightMode(
        if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
    )
}

// this function will make us can press on user item when press back button to home fragment
internal fun Fragment.handleBackPressedEvent(
    controller: NavController,
    handleAction: () -> Unit
) {
    requireActivity().apply {
        onBackPressedDispatcher.addCallback {
            when (controller.currentDestination?.id) {
                R.id.nav_HomeFragment -> finish()
                R.id.modifyUserFragment -> showDialog(requireContext(), handleAction){
                    controller.navigate(R.id.userInfoFragment)
                }
                R.id.modifyPersonalFragment ->  showDialog(requireContext(), handleAction){
                    controller.navigate(R.id.personalFragment)
                }
                else -> controller.navigate(R.id.nav_HomeFragment)
            }
        }
    }

}

private fun showDialog(
    context: Context,
    handleAction: () -> Unit,
    navigate: () -> Unit
) {
    AlertDialog.Builder(context)
        .setCancelable(true)
        .setTitle(context.getString(R.string.save_the_change_title_dialog))
        .setNegativeButton(context.getString(R.string.no)) { dialog, which ->
            navigate()
        }
        .setPositiveButton(context.getText(R.string.yes)) { dialog, which ->
            handleAction()
        }
        .create()
        .show()
}

internal fun handleNavigationBack(controller: NavController, drawerLayout: DrawerLayout){
    when (controller.currentDestination?.id) {
        R.id.modifyUserFragment -> {
            controller.navigate(R.id.userInfoFragment)
        }
        R.id.modifyPersonalFragment ->{
            controller.navigate(R.id.personalFragment)
        }
        else -> {
            if (drawerLayout.isOpen) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }
}

fun View.hideKeyboard(): Boolean {
    try {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return inputMethodManager.hideSoftInputFromWindow(applicationWindowToken, 0)
    } catch (ignored: RuntimeException) { }
    return false
}