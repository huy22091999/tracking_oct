package com.oceantech.tracking.data.repository

import com.oceantech.tracking.data.model.Notification
import com.oceantech.tracking.data.network.NotificationApi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import retrofit2.http.GET

class NotificationRepository(
    val api: NotificationApi
) {
    fun getAllByUser(): Observable<List<Notification>> =
        api.getAllByUser().subscribeOn(Schedulers.io())
}