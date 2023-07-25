package com.oceantech.tracking.ui.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.oceantech.tracking.utils.showNotification

class MessagingService:FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title
        val content = message.notification?.body

        showNotification(this,title!!,content!!)
    }
}