package com.oceantech.tracking.utils

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.fragment.app.commitNow
import androidx.viewbinding.ViewBinding
import com.oceantech.tracking.R
import com.oceantech.tracking.databinding.DialogLoginBinding
import com.oceantech.tracking.ui.MainActivity
import com.oceantech.tracking.ui.security.SecurityViewAction
import timber.log.Timber
import java.text.SimpleDateFormat
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
inline fun FragmentManager.commitTransaction(allowStateLoss: Boolean = false, func: FragmentTransaction.() -> FragmentTransaction) {
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
    supportFragmentManager
        .commitTransaction(allowStateLoss) {
        option?.invoke(this)
        replace(frameId, fragmentClass, null, tag)
            .addToBackStack(tag)
    }

}
fun validateEmail(email: String): Boolean {
    val emailRegex = Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")
    return emailRegex.matches(email)
}

fun formatDate(input:String):Date{
    //formatting time to Vietnam's time
    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    val date: Date = formatter .parse(input)
//    date.hours = date.hours - 7
//    val formatter2 = SimpleDateFormat("dd/MM/yyyy-HH:mm")
//    val formattedDate: String = formatter2.format(date)
    return date
}

fun showNotification(context: Context,title:String, body:String) {
    val builder = NotificationCompat.Builder(context, MainActivity.NOTIFICATION_CHANNEL_ID)

    builder.apply {
        setSmallIcon(R.drawable.ic_notification)
        setContentTitle(title)
        setContentText(body)
        priority = NotificationCompat.PRIORITY_MAX
    }

    //big text style
    val style = NotificationCompat.BigTextStyle()
    style.bigText(title)
    style.setBigContentTitle(title)
    style.setSummaryText(body)

    builder.setStyle(style)
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    builder.setChannelId(MainActivity.NOTIFICATION_CHANNEL_ID)

    manager.notify(Random().nextInt(), builder.build())
}

fun initialAlertDialog(context: Context, accept:()->Unit, refuse:()->Unit,descText:String, acceptText:String, refuseText:String):AlertDialog{
    val builder = AlertDialog.Builder(context)
    val view: ViewBinding = DialogLoginBinding.inflate(LayoutInflater.from(context))
    builder.setView(view.root)

    val alertDialog = builder.create()

    with(view as DialogLoginBinding){
        view.dialogTitle.text = descText
        view.returnSignIn.text = acceptText
        view.back.text = refuseText
        view.returnSignIn.setOnClickListener {
            if(alertDialog.isShowing){
                alertDialog.dismiss()
                accept()
                alertDialog.cancel()
            }
        }
        view.back.setOnClickListener {
            if(alertDialog.isShowing){
                alertDialog.dismiss()
                alertDialog.cancel()
                refuse()
            }
        }
    }
    return alertDialog
}
