package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.TokenResponse
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.model.UserCredentials
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TrankingApi {

    @GET("tracking")
    fun getAllTracking(): Observable<ArrayList<Tracking>>

    @POST("tracking")
    fun addTracking(@Body tracking: Tracking): Observable<Tracking>

    @POST("tracking/{id}")
    fun updateTracking(@Path("id") id : Int,@Body tracking: Tracking): Observable<Tracking>

    @DELETE("tracking/{id}")
    fun deleteTracking(@Path("id") id : Int): Observable<Tracking>

}