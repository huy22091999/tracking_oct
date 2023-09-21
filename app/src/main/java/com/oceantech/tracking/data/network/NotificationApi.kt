package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.Notification
import io.reactivex.Observable

interface NotificationApi {
    fun getAllByUser():Observable<List<Notification>>
}