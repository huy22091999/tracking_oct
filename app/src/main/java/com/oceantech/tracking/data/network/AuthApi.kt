package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.TokenResponse
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.model.UserCredentials
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("oauth/token")
    fun loginWithRefreshToken(@Body credentials: UserCredentials):Call<TokenResponse>
    @POST("oauth/token")
    fun oauth(@Body credentials: UserCredentials): Observable<TokenResponse>

    @POST("public/sign")
    fun signup(@Body user: User):Observable<User>
    companion object {
        val CLIENT_ID = "core_client" //"core_client"

        val CLIENT_SECRET = "secret" //"secret"

        val GRANT_TYPE_PASSWORD = "password"

        val GRANT_TYPE_REFRESH = "refresh_token"

        val DEFAULT_SCOPES = "read write delete"
    }
}