package com.oceantech.tracking.data.repository

import com.oceantech.tracking.data.model.NotificationResponse
import com.oceantech.tracking.data.network.NotificationApi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class NotificationRepository (
    val api:NotificationApi
) {
    fun getAllByUser():Observable<List<NotificationResponse>> = api.getAllByUser().subscribeOn(Schedulers.io())
}