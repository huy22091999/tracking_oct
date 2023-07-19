package com.oceantech.tracking

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.airbnb.mvrx.Mavericks
import com.oceantech.tracking.ui.MainActivity
import com.oceantech.tracking.utils.LocalHelper
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

/*
    Use HiltAndroidApp so that we don't need to inject function
*/
@HiltAndroidApp
open class TrackingApplication : Application() {

    @Inject
    lateinit var localHelper: LocalHelper


    override fun onCreate() {
        Mavericks.initialize(this)
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val name = getString(R.string.app_name)
        val descriptionText = getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(getString(R.string.noti_channel_id), name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }




}

