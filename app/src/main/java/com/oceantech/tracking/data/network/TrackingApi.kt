package com.oceantech.tracking.data.network

import android.database.Observable
import com.oceantech.tracking.data.model.Tracking
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TrackingApi {

    @GET("tracking")
    fun getAllTracking(): io.reactivex.Observable<List<Tracking>>

    @POST("tracking")
    fun saveTracking(@Body tracking: Tracking): io.reactivex.Observable<Tracking>

    @POST("tracking/{id}")
    fun updateTracking(@Body tracking: Tracking, @Path("id") id: Int): io.reactivex.Observable<Tracking>

    @DELETE("tracking/{id}")
    fun deleteTracking(@Path("id") id: Int): io.reactivex.Observable<Tracking>

}