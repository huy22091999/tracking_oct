package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.Page
import com.oceantech.tracking.data.model.TokenResponse
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.model.UserFilter
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface UserApi {
    @GET("users/lock/{id}")
    fun blockUser(@Path("id") id:Int):Observable<User>
    @POST("users/searchByPage")
    suspend fun searchByPage(@Body filter:UserFilter):Page<User>
    @POST("public/sign")
    fun sign(@Body user: User):Observable<User>
    @GET("users/get-user-current")
    fun getCurrentUser(): Observable<User>
    @GET("users/{id}")
    fun getUserById(@Path("id") id:String):Observable<User>
    @GET("users/get-user-current")
    fun getCurrentUserTest(): Call<User>
    @GET("users/token-device")
    fun edit(@Query("tokenDevice") tokenDevice:String):Observable<User>
    @GET("users/get-all-user")
    fun getAllUser():Observable<List<User>>
    @POST("users/update-myself")
    fun updateMyself(@Body user: User):Observable<User>
    @POST("users/update/{id}")
    fun edit(@Path("id") id:Int, @Body user: User):Observable<User>
}