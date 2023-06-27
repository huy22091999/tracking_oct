package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.TokenResponse
import com.oceantech.tracking.data.model.User
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface UserApi {
    @GET("users/get-user-current")
    suspend fun getCurrentUser(): User
    @GET("users/get-user-current")
    fun getCurrentUserTest(): User
    @POST("public/sign")
    suspend fun createUpdateUser(@Body user: User): TokenResponse

    @GET("users/get-all-user")
    suspend fun getAllUsers(): List<User>
}