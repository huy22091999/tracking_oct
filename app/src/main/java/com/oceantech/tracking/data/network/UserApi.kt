package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.Page
import com.oceantech.tracking.data.model.Pageable
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

    @POST("users/update-myself")
    fun updateUser(@Body user: User): Observable<User>

    @POST("users/searchByPage")
    suspend fun getUserByPage(@Body filter: Pageable): Page<User>
}