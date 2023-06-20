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
    fun save(@Body tracking: Tracking): Observable<Tracking>

    @POST("tracking/{id}")
    fun update(@Path("id") id:Int, @Body tracking: Tracking): Observable<Tracking>

    @DELETE("tracking/{id}")
    fun delete(@Path("id") id:Int): Observable<Tracking>

    @GET("tracking")
    fun getAllByUser():Observable<List<Tracking>>
}