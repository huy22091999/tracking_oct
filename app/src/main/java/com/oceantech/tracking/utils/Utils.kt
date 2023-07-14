package com.oceantech.tracking.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.location.Location
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import androidx.activity.addCallback
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oceantech.tracking.R
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.ui.security.LoginActivity
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
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
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    val date = LocalDate.parse(localDate, formatter)
    val zonedDateTime = date.atStartOfDay(ZoneId.systemDefault())
    return zonedDateTime.toInstant().toString()
}


//Update language of app
fun Activity.changeLanguage(localHelper: LocalHelper, language: String) {
    localHelper.setLanguage(baseContext, language)
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
    @LayoutRes layoutId: Int,
    progress: Long = 0,
    max: Long = 0
): Notification {
    val views = RemoteViews(context.packageName, layoutId)
    if (progress > 0 && max > 0) views.setProgressBar(
        R.id.downloadPB,
        max.toInt(),
        progress.toInt(),
        false
    )
    return NotificationCompat.Builder(context, id)
        .setContentTitle(contentTitle)
        .setSmallIcon(R.drawable.download_icon)
        .setContentText(contentText)
        .setStyle(NotificationCompat.DecoratedCustomViewStyle())
        .setCustomContentView(views)
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

class ItemDecoration(
    private val distance: Int
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = distance
        outRect.top = distance
        outRect.right = distance
        outRect.left = distance
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
internal fun Fragment.handleBackPressedEvent(controller: NavController) {
    requireActivity().apply {
        onBackPressedDispatcher.addCallback {
            when (controller.currentDestination?.id) {
                R.id.nav_HomeFragment -> finish()
                R.id.modifyUserFragment -> controller.navigate(R.id.userInfoFragment)
                R.id.modifyPersonalFragment -> controller.navigate(R.id.personalFragment)
                else -> controller.navigate(R.id.nav_HomeFragment)
            }
        }
    }
}