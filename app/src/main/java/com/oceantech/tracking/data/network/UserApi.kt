package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.Page
import com.oceantech.tracking.data.model.Pageable
import com.oceantech.tracking.data.model.User
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface UserApi {
    @GET("users/get-user-current")
    fun getCurrentUser(): Observable<User>
    @GET("users/{id}")
    fun getCurrentUserById(@Path("id")id: String): Observable<User>

    @GET("users/get-user-current")
    fun getCurrentUserTest(): Call<User>

    @GET("users/get-all-user")
    fun getAllUser(): Call<User>

    @POST("users/searchByPage")
    suspend fun getUserByPage(@Body filter: Pageable): Page<User>


    @POST("users/update-myself")
    fun updateUser(@Body user: User): Observable<User>

    @POST("users/update/{id}")
    fun updateUserById(@Path("id")id: String , @Body user: User): Observable<User>

    @GET("users/lock/{id}")
    fun blockUser(@Path("id")id: String): Observable<User>

}