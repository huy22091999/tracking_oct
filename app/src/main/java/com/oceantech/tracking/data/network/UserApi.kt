package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.Page
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.model.UserFilter
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT


interface UserApi {
    @GET("users/get-user-current")
    fun getCurrentUser(): Observable<User>
    @GET("users/get-user-current")
    fun getCurrentUserTest(): Call<User>
    @POST("users/searchByPage")
    suspend fun getAllUser(@Body filter: UserFilter): Page<User>

    @POST("users/update-myself")
    fun updateMyself(@Body user: User): Observable<User>
}