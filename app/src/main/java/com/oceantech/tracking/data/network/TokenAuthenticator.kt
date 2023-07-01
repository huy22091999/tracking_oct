package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.TokenResponse
import com.oceantech.tracking.data.model.UserCredentials
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Call
import retrofit2.Callback
import javax.inject.Inject

class TokenAuthenticator (
    accessToken:String,
    private val api: AuthApi,
    private val sessionManager: SessionManager
) : Authenticator {

    private var mAccessToken = accessToken
    override fun authenticate(route: Route?, response: Response): Request {
        if(response.code == 200){
            var accessToken = ""
            var refreshToken = ""
            getUpdatedToken().enqueue(object : Callback<TokenResponse>{
                override fun onResponse(
                    call: Call<TokenResponse>,
                    response: retrofit2.Response<TokenResponse>
                ) {
                    if(response.body()?.accessToken != null){
                        accessToken = response.body()?.accessToken.toString()
                        refreshToken = response.body()?.refreshToken.toString()
                        sessionManager.saveAuthToken(accessToken)
                        sessionManager.saveAuthTokenRefresh(refreshToken)
                    }
                }

                override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                }
            })
            return response.request.newBuilder()
                .header(
                    "Authorization",
                    "Bearer $accessToken"
                )
                .build()
        }
        return response.request.newBuilder()
            .header(
                "Authorization",
                if(!mAccessToken.isNullOrEmpty()) "Bearer $mAccessToken" else "Basic Y29yZV9jbGllbnQ6c2VjcmV0"
            )
            .build()
    }

    private fun getUpdatedToken(): Call<TokenResponse> {
        val credentials =
            UserCredentials(
                AuthApi.CLIENT_ID,
                AuthApi.CLIENT_SECRET,
                username = "",
                password = "",
                refreshToken = sessionManager.fetchAuthTokenRefresh(),
                AuthApi.GRANT_TYPE_REFRESH
            )

        return api.loginWithRefreshToken(credentials)
    }
}

//    override fun authenticate(route: Route?, response: Response): Request? {
//
//        if (sessionManager.fetchAuthTokenRefresh() != null) {
//            return runBlocking {
//                var token = ""
//                val tokenResponse = getUpdatedToken()
//                tokenResponse.enqueue(object : Callback<TokenResponse> {
//                    override fun onResponse(
//                        call: Call<TokenResponse>,
//                        response1: retrofit2.Response<TokenResponse>
//                    ) {
//                        if (response1.body() != null) {
//                            token = response1.body()?.accessToken.toString()
//                            sessionManager.saveAuthToken(token)
//                            sessionManager.saveAuthTokenRefresh(response1.body()?.refreshToken.toString())
//                        }
//                    }
//
//                    override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
//                    }
//                })
//                response.request.newBuilder().header("Authorization", "Bearer $token")
//                    .build()
//            }
//
//        } else
//            return response.request.newBuilder()
//                .header("Authorization", "Basic Y29yZV9jbGllbnQ6c2VjcmV0")
//                .build()
//
//    }
//
//    private fun getUpdatedToken(): Call<TokenResponse> {
//        val credentials =
//            UserCredentials(
//                AuthApi.CLIENT_ID,
//                AuthApi.CLIENT_SECRET,
//                username = "",
//                password = "",
//                refreshToken = sessionManager.fetchAuthTokenRefresh(),
//                AuthApi.GRANT_TYPE_REFRESH
//            )
//
//        return api.loginWithRefreshToken(credentials)
//    }
