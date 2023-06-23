package com.oceantech.tracking.data.network

import android.content.Context
import android.util.Log
import com.oceantech.tracking.data.model.TokenResponse
import com.oceantech.tracking.data.model.UserCredentials
import com.oceantech.tracking.ui.security.UserPreferences
import okhttp3.Authenticator
import okhttp3.Credentials
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Call

class TokenAuthenticator(
    accessToken: String,
//    private val context: Context,
//    val api: AuthApi
) : Authenticator {

    private val mAccessToken = accessToken

    override fun authenticate(route: Route?, response: Response): Request {
//        Log.i("Login", "access: $mAccessToken")
        return response.request.newBuilder()
            .header(
                "Authorization",
                if (mAccessToken.isNotEmpty()) "Bearer $mAccessToken" else "Basic Y29yZV9jbGllbnQ6c2VjcmV0"
            )
            .build()
    }

//    private fun refreshToken(): Call<TokenResponse> = api.loginWithRefreshToken(
//        UserCredentials(
//            AuthApi.CLIENT_ID,
//            AuthApi.CLIENT_SECRET,
//            username = "",
//            password = "",
//            refreshToken = sessionManager.fetchAuthTokenRefresh(),
//            AuthApi.GRANT_TYPE_REFRESH
//        )
//    )

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


