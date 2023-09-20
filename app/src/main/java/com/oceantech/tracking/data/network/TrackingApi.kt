package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.TimeSheet
import com.oceantech.tracking.data.model.Tracking
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TrackingApi {
    @GET("tracking")
    fun getTracking(): Observable<List<Tracking>>
    @POST("tracking/{id}")
    fun update(@Path("id") id:Int, @Body tracking: Tracking): Observable<Tracking>

    @DELETE("tracking/{id}")
    fun delete(@Path("id") id:Int): Observable<Tracking>
    @POST("tracking")
    fun saveTracking(@Body tracking: Tracking): Observable<Tracking>
}