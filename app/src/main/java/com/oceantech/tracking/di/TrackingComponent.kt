package com.oceantech.tracking.di

import android.content.Context
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.oceantech.tracking.TrackingApplication
import com.oceantech.tracking.ui.MainActivity
import com.oceantech.tracking.ui.security.LoginActivity
import com.oceantech.tracking.ui.security.SplashActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = [
    ViewModelModule::class,
    FragmentModule::class,
    NetWorkModule::class
])
@Singleton
interface TrackingComponent {
    fun inject(trackingApplication: TrackingApplication)
    fun inject(mainActivity: MainActivity)
    fun inject(loginActivity: LoginActivity)
    fun inject(splashActivity: SplashActivity)
    fun fragmentFactory(): FragmentFactory
    fun viewModelFactory(): ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): TrackingComponent
    }
}