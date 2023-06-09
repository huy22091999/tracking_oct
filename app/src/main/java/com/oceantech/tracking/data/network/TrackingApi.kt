package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.Tracking
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface TrackingApi {
    @POST("tracking")
    fun tracking(@Body tracking: Tracking) : Observable<Tracking>
}