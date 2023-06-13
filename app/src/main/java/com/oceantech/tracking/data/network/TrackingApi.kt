package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.Tracking
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TrackingApi {
    @POST("tracking")
    fun tracking(@Body tracking: Tracking) : Observable<Tracking>
    @POST("tracking/{id}")
    fun updateTracking(@Body tracking: Tracking, @Path("id") id: Int?) : Observable<Tracking>

    @GET("tracking")
    fun getAllTracking() : Observable<MutableList<Tracking>>

    @DELETE("tracking/{id}")
    fun deleteTracking(@Path("id") id: Int) : Observable<Tracking>
}