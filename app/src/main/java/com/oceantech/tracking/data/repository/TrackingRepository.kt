package com.oceantech.tracking.data.repository

import android.database.Observable
import com.oceantech.tracking.data.model.Role
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.network.TrackingApi
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.subscribeOn
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Arrays
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingRepository @Inject constructor(
    private val api: TrackingApi
) {

    fun getAllTracking(): Flow<List<Tracking>> = flow {
        emit(api.getAllTracking())
    }.flowOn(Dispatchers.IO)

    fun saveTracking(content: String): Flow<Tracking> = flow {
        emit(api.saveTracking(
            Tracking(
                content,
                Instant.from(DateTimeFormatter.ISO_INSTANT.parse("2023-06-19T01:41:45.341Z"))
                    .toString(),
                null,
                null
            )
        ))
    }.flowOn(Dispatchers.IO)

    fun updateTracking(tracking: Tracking, id: Int): Flow<Tracking> = flow {
        emit(api.updateTracking(tracking, id))
    }.flowOn(Dispatchers.IO)

    fun deleteTracking(id: Int): Flow<Tracking> = flow {
        emit( api.deleteTracking(id))
    }.flowOn(Dispatchers.IO)

}