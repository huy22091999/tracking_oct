package com.oceantech.tracking.data.network

import android.database.Observable
import com.oceantech.tracking.data.model.Tracking
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TrackingApi {

    @GET("tracking")
    suspend fun getAllTracking(): List<Tracking>

    @POST("tracking")
    suspend fun saveTracking(@Body tracking: Tracking): Tracking

    @POST("tracking/{id}")
    suspend fun updateTracking(@Body tracking: Tracking, @Path("id") id: Int): Tracking

    @DELETE("tracking/{id}")
    suspend fun deleteTracking(@Path("id") id: Int): Tracking

}