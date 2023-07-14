package com.oceantech.tracking.di

import android.content.Context
import com.oceantech.tracking.data.network.*
import com.oceantech.tracking.data.repository.*
import com.oceantech.tracking.ui.security.UserPreferences
import com.oceantech.tracking.utils.LocalHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.sql.Time

@Module
@InstallIn(SingletonComponent::class)
object NetWorkModule {
    @Provides
    fun providerLocaleHelper(): LocalHelper = LocalHelper()

    @Provides
    fun providerRemoteDateSource(): RemoteDataSource = RemoteDataSource()


    @Provides
    fun providerUserPreferences(@ApplicationContext context: Context): UserPreferences = UserPreferences(context)

    @Provides
    fun providerSessionManager(@ApplicationContext context: Context): SessionManager = SessionManager(context)


    @Provides
    fun providerAuthApi(
        remoteDataSource: RemoteDataSource,
        @ApplicationContext context: Context
    ) = remoteDataSource.buildApi(AuthApi::class.java, context)


    @Provides
    fun providerAuthRepository(
        userPreferences: UserPreferences,
        api: AuthApi,
        @ApplicationContext context: Context
    ): AuthRepository = AuthRepository(api, userPreferences, context)


    @Provides
    fun providerUserApi(
        remoteDataSource: RemoteDataSource,
        @ApplicationContext context: Context
    ) = remoteDataSource.buildApi(UserApi::class.java, context)

    @Provides
    fun providerUserRepository(
        api: UserApi
    ): UserRepository = UserRepository(api)

    @Provides
    fun providerTrackingApi(
        remoteDataSource: RemoteDataSource,
        @ApplicationContext context: Context
    ) = remoteDataSource.buildApi(TrackingApi::class.java, context)

    @Provides
    fun providerTrackingRepository(
        api: TrackingApi
    ): TrackingRepository = TrackingRepository(api)

    @Provides
    fun providerTimeSheetApi(
        remoteDataSource: RemoteDataSource,
        @ApplicationContext context: Context
    ) = remoteDataSource.buildApi(TimeSheetApi::class.java, context)
    @Provides
    fun providerTimeSheetRepository(
        api: TimeSheetApi
    ) = TimeSheetRepository(api)

    @Provides
    fun providerPublicApi(
        remoteDataSource: RemoteDataSource,
        @ApplicationContext context: Context
    ) = remoteDataSource.buildApi(PublicApi::class.java, context)

    @Provides
    fun providerPublicRepository(
        api: PublicApi
    ) = PublicRepository(api)
}