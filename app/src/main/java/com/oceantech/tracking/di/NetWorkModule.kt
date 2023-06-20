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
        api: AuthApi,
        configApi: ConfigApi
    ): AuthRepository = AuthRepository(api, configApi, userPreferences)

    @Provides
    fun provideSignApi(remoteDataSource: RemoteDataSource) =
        remoteDataSource.buildApiSignIn(SignApi::class.java)

    @Provides
    fun providerSignInRepository(api: SignApi): SignRepository = SignRepository(api)

    @Provides
    fun providerTracking(remoteDataSource: RemoteDataSource, context: Context) =
        remoteDataSource.buildApi(TrackingApi::class.java, context)

    @Provides
    fun providerTrackingRepository(api: TrackingApi): TrackingRepository = TrackingRepository(api)

    @Provides
    fun provideTimeSheetApi(remoteDataSource: RemoteDataSource, context: Context) =
        remoteDataSource.buildApi(TimeSheetApi::class.java, context)

    @Provides
    fun provideTimeSheetRepo(api: TimeSheetApi, ipApi: IpApi): TimeSheetRepository =
        TimeSheetRepository(api, ipApi)

    @Provides
    fun providerUserApi(
        remoteDataSource: RemoteDataSource,
        context: Context
    ) = remoteDataSource.buildApi(UserApi::class.java, context)

    @Provides
    fun providerIpApi(remoteDataSource: RemoteDataSource, context: Context) =
        remoteDataSource.buildApiIp(IpApi::class.java, context)

    @Provides
    fun providerConfigApi(remoteDataSource: RemoteDataSource, context: Context) =
        remoteDataSource.buildApi(ConfigApi::class.java, context)

    @Provides
    fun providerUserRepository(
        api: UserApi
    ): UserRepository = UserRepository(api)
}