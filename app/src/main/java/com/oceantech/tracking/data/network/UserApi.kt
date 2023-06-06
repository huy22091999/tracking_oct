package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.TokenResponse
import com.oceantech.tracking.data.model.User
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface UserApi {
    @POST("public/sign")
    fun sign(@Body user: User):Observable<TokenResponse>
    @GET("users/get-user-current")
    fun getCurrentUser(): Observable<User>
    @GET("users/get-user-current")
    fun getCurrentUserTest(): Call<User>
    @GET("users/get-all-user")
    fun getAllUser():Observable<List<User>>
}