package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.User
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface SignApi {
    @POST("public/sign")
    fun signIn(@Body user: User) : Observable<User>
}