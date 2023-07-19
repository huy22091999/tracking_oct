package com.oceantech.tracking.core

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.remoteMessage
import com.oceantech.tracking.R
import com.oceantech.tracking.ui.MainActivity
import com.oceantech.tracking.ui.security.LoginActivity
import com.oceantech.tracking.ui.security.SplashActivity
import com.oceantech.tracking.utils.createNotification
import java.lang.Exception
@SuppressLint("LogNotTimber")
class TrackingCloudServices : FirebaseMessagingService() {


    override fun onNewToken(token: String) {
        Log.i("Token", "Refresh token: $token")

    }

    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title
        val body = message.notification?.body

        sendNotification(title, body)

    }

    private fun sendNotification(title: String?, body: String?) {
        title?.let { title ->
            val notification = body?.let {body ->
                createNotification(
                    getString(R.string.noti_channel_id),
                    applicationContext,
                    title,
                    body,
                    iconId = R.drawable.notification_icon
                )
            }

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(0, notification)
        }
    }

    override fun onMessageSent(msgId: String) {
        super.onMessageSent(msgId)
    }

    override fun onSendError(msgId: String, exception: Exception) {
        Log.i("Message", "$msgId - ${exception.message}")
    }
}