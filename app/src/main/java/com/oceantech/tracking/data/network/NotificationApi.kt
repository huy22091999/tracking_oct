package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.Notification
import retrofit2.http.GET

interface NotificationApi {

    @GET("notifications")
    suspend fun getNotifications(): List<Notification>
}