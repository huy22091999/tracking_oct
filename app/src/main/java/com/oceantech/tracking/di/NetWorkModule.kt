package com.oceantech.tracking.di

import android.content.Context
import com.oceantech.tracking.data.network.*
import com.oceantech.tracking.data.repository.*
import com.oceantech.tracking.ui.security.UserPreferences
import com.oceantech.tracking.utils.LocalHelper
import dagger.Module
import dagger.Provides

@Module
object NetWorkModule {
    @Provides
    fun providerLocaleHelper(): LocalHelper = LocalHelper()

    @Provides
    fun providerRemoteDateSource(): RemoteDataSource = RemoteDataSource()


    @Provides
    fun providerUserPreferences(context: Context): UserPreferences = UserPreferences(context)


    @Provides
    fun providerAuthApi(
        remoteDataSource: RemoteDataSource,
        context: Context
    ) = remoteDataSource.buildApi(AuthApi::class.java, context)


    @Provides
    fun providerAuthRepository(
        userPreferences: UserPreferences,
        api: AuthApi
    ): AuthRepository = AuthRepository(api, userPreferences)


    @Provides
    fun providerUserApi(
        remoteDataSource: RemoteDataSource,
        context: Context
    ) = remoteDataSource.buildApi(UserApi::class.java, context)

    @Provides
    fun providerUserRepository(
        api: UserApi
    ): UserRepository = UserRepository(api)

    @Provides
    fun providerTrackingApi(
        remoteDataSource: RemoteDataSource,
        context: Context
    ) = remoteDataSource.buildApi(TrackingApi::class.java, context)

    @Provides
    fun providerTrackingRepository(
        api: TrackingApi
    ): TrackingRepository = TrackingRepository(api)

}