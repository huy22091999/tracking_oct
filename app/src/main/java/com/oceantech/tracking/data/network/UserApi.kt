package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.User
import io.reactivex.Observable
import retrofit2.http.GET


interface UserApi {
    @GET("api/users/getCurrentUser")
    fun getCurrentUser(): Observable<User>

}