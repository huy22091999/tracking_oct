package com.oceantech.tracking.data.repository

import com.oceantech.tracking.data.model.Notification
import com.oceantech.tracking.data.network.NotificationApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val api: NotificationApi
) {

    fun getNotifications(): Flow<List<Notification>> = flow {
        emit(api.getNotifications())
    }.flowOn(Dispatchers.IO)
}