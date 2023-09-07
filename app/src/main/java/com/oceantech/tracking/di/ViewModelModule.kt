package com.oceantech.tracking.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oceantech.tracking.ui.home.TestViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {
    @Binds
    fun bindViewModelFactory(factory: TrackingViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(TestViewModel::class)
    fun bindViewModel(viewModel: TestViewModel): ViewModel

}