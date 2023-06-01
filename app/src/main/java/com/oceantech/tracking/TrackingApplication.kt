package com.oceantech.tracking

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.oceantech.tracking.di.DaggerTrackingComponent
import com.oceantech.tracking.di.TrackingComponent
import com.oceantech.tracking.ui.MainActivity
import com.oceantech.tracking.utils.LocalHelper
import timber.log.Timber
import javax.inject.Inject


open class TrackingApplication : Application() {
    val trackingComponent: TrackingComponent by lazy {
        initializeComponent()
    }

    @Inject
    lateinit var localHelper: LocalHelper
    open fun initializeComponent(): TrackingComponent {
        // Creates an instance of AppComponent using its Factory constructor
        // We pass the applicationContext that will be used as Context in the graph
        return DaggerTrackingComponent.factory().create(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        trackingComponent.inject(this)
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

        val channel = NotificationChannel(MainActivity.NOTIFICATION_CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

}

