package com.oceantech.tracking

import android.app.Application
import com.oceantech.tracking.di.DaggerTrackingComponent
import com.oceantech.tracking.di.TrackingComponent
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
    }
}

