package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.Page
import com.oceantech.tracking.data.model.Pageable
import com.oceantech.tracking.data.model.UpLoadImage
import com.oceantech.tracking.data.model.User
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path


interface UserApi {
    @GET("users/lock/{id}")
    fun blockUser(@Path("id") id: Int): Observable<User>

    @GET("users/get-user-current")
    fun getCurrentUser(): Observable<User>

    @GET("users/get-user-current")
    fun getCurrentUserTest(): Call<User>

    @POST("users/update-myself")
    fun updateUser(@Body user: User): Observable<User>

    @POST("users/update/{id}")
    fun edit(@Path("id") id: Int, @Body user: User): Observable<User>

    @POST("users/searchByPage")
    suspend fun getUserByPage(@Body filter: Pageable): Page<User>


}