package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface UserApi {
    @GET("users/get-user-current")
    suspend fun getCurrentUser(): User
    @GET("users/get-user-current")
    fun getCurrentUserTest(): User
    @POST("public/sign")
    suspend fun createUpdateUser(@Body user: User): User

    @GET("users/get-all-user")
    suspend fun getAllUsers(): List<User>

    @GET("users/")
    suspend fun getDevice(@Query("tokenDevice") tokenDevice: String): User

    @GET("users/lock/{id}")
    suspend fun lockUser(@Path("id") id: Int): User

    @POST("users/update-myself")
    suspend fun updateMyself(@Body user: User) : User

    @POST("users/update/{id}")
    suspend fun updateUser(@Body user: User, @Path("id") id: Int): User

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Int): User
}