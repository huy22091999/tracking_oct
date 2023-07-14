package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.TokenResponse
import com.oceantech.tracking.data.model.User
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface UserApi {
    @GET("users/get-user-current")
    suspend fun getCurrentUser(): User
    @GET("users/get-user-current")
    fun getCurrentUserTest(): User
    @POST("public/sign")
    suspend fun createUpdateUser(@Body user: User): User

    @GET("users/get-all-user")
    suspend fun getAllUsers(): List<User>

    @GET("users/token-device/{tokenDevice}")
    suspend fun getDevice(@Path("tokenDevice") tokenDevice: String): User

    @GET("users/block/{id}")
    suspend fun getBlockUser(@Path("id") id: Int): User

    @POST("users/update-myself")
    suspend fun updateMyself(@Body user: User) : User

    @POST("users/update/{id}")
    suspend fun updateUser(@Body user: User, @Path("id") id: Int): User
}