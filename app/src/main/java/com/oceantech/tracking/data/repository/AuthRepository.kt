package com.oceantech.tracking.data.repository

import android.content.Context
import com.oceantech.tracking.data.model.TokenResponse
import com.oceantech.tracking.data.model.UserCredentials
import com.oceantech.tracking.data.network.AuthApi
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.ui.security.UserPreferences
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    val api: AuthApi,
    private val preferences: UserPreferences,
    val context: Context
) {
    //    private val sessionManager = SessionManager(context)
    fun login(username: String, password: String): Flow<TokenResponse> = flow {
        emit(
            api.oauth(
                UserCredentials(
                    AuthApi.CLIENT_ID,
                    AuthApi.CLIENT_SECRET,
                    username,
                    password,
                    null,
                    AuthApi.GRANT_TYPE_PASSWORD
                )
            )
        )
    }.flowOn(Dispatchers.IO)

    suspend fun saveAccessTokens(tokens: TokenResponse) {
        if (tokens.accessToken == null || tokens.refreshToken == null) {
            return
        }
        preferences.saveAccessTokens(tokens.accessToken, tokens.refreshToken)
    }


}