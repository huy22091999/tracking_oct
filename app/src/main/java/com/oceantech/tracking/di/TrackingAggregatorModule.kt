package com.oceantech.tracking.di

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/*
    Migrate Dagger 2 to Hilt (From Component to Aggregator)
 */
@InstallIn(SingletonComponent::class)
@Module(
    includes = [
        ViewModelModule::class,
        FragmentModule::class,
        NetWorkModule::class
    ]
)
interface TrackingAggregatorModule {

}