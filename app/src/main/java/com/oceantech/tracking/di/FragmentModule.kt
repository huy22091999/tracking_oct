package com.oceantech.tracking.di

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.oceantech.tracking.ui.home.HomeFragment
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap

@Module
@InstallIn(FragmentComponent::class)
interface FragmentModule {
    @Binds
    fun bindFragmentFactory(factory: VectorFragmentFactory): FragmentFactory
    @Binds
    @IntoMap
    @FragmentKey(HomeFragment::class)
    fun bindHomeFragment(homeFragment: HomeFragment): Fragment


}