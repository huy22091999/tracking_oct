package com.oceantech.tracking.di

import android.content.Context
import com.oceantech.tracking.data.network.*
import com.oceantech.tracking.data.repository.AuthRepository
import com.oceantech.tracking.data.repository.ImageRepository
import com.oceantech.tracking.data.repository.NotificationRepository
import com.oceantech.tracking.data.repository.PublicRepository
import com.oceantech.tracking.data.repository.TimeSheetRepository
import com.oceantech.tracking.data.repository.TokenRepository
import com.oceantech.tracking.data.repository.TrackingRepository
import com.oceantech.tracking.data.repository.UserRepository
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
        api: UserApi,
        context: Context
    ): UserRepository = UserRepository(api, context)

    @Provides
    fun providerTrackingApi(
        remoteDataSource: RemoteDataSource,
        context: Context
    ) = remoteDataSource.buildApi(TrackingApi::class.java, context)

    @Provides
    fun providerTrackingRepository(
        api: TrackingApi
    ): TrackingRepository = TrackingRepository(api)

    @Provides
    fun providerTimeSheetApi(
        remoteDataSource: RemoteDataSource,
        context: Context
    ) = remoteDataSource.buildApi(TimeSheetApi::class.java, context)

    @Provides
    fun providerTimeSheetRepository(
        api:TimeSheetApi
    ): TimeSheetRepository = TimeSheetRepository(api)

    @Provides
    fun providerPublicApi(
        remoteDataSource: RemoteDataSource,
        context: Context
    ) = remoteDataSource.buildApi(PublicApi::class.java, context)

    @Provides
    fun providerPublicRepository(
        api: PublicApi
    ):PublicRepository = PublicRepository(api)

    @Provides
    fun providerImageApi(
        remoteDataSource: RemoteDataSource,
        context: Context
    ) = remoteDataSource.buildApi(ImageApi::class.java,context)

    @Provides
    fun providerImageRepository(
        api: ImageApi
    ):ImageRepository = ImageRepository(api)

    @Provides
    fun providerNotificationApi(
        remoteDataSource: RemoteDataSource,
        context: Context
    ) = remoteDataSource.buildApi(NotificationApi::class.java,context)

    @Provides
    fun providerNotificationRepository(
        api:NotificationApi
    ):NotificationRepository = NotificationRepository(api)

    @Provides
    fun providerTokenApi(
        remoteDataSource: RemoteDataSource,
        context: Context
    ) = remoteDataSource.buildApi(TokenApi::class.java, context)

    @Provides
    fun providerTokenRepository(
        api: TokenApi
    ):TokenRepository = TokenRepository(api)
}