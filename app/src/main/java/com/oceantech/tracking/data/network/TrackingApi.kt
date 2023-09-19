package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.TimeSheet
import com.oceantech.tracking.data.model.Tracking
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TrackingApi {
    @GET("tracking")
    fun getTracking(): Observable<List<Tracking>>

    @POST("tracking")
    fun saveTracking(@Body tracking: Tracking): Observable<Tracking>
}