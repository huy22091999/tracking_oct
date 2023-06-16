package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.TokenResponse
import com.oceantech.tracking.data.model.User
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface UserApi {
    @GET("users/get-user-current")
    fun getCurrentUser(): Observable<User>
    @GET("users/get-user-current")
    fun getCurrentUserTest(): Call<User>
    @POST("public/sign")
    fun createUpdateUser(@Body user: User): Observable<TokenResponse>
}