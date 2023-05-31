package com.oceantech.tracking.di

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module

@Module
interface ViewModelModule{
    @Binds
    fun bindViewModelFactory(factory: TrackingViewModelFactory): ViewModelProvider.Factory
}