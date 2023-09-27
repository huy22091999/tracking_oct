package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.Notification
import io.reactivex.Observable
import retrofit2.http.GET

interface NotificationApi {
    @GET("notifications")
    fun getAllByUser(): Observable<List<Notification>>
}