package com.oceantech.tracking.data.repository

import android.database.Observable
import com.oceantech.tracking.data.model.Role
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.network.TrackingApi
import io.reactivex.schedulers.Schedulers
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

    fun getAllTracking(): io.reactivex.Observable<List<Tracking>> =
        api.getAllTracking().subscribeOn(Schedulers.io())

    fun saveTracking(content: String): io.reactivex.Observable<Tracking> =
        api.saveTracking(
            Tracking(
                content,
                Instant.from(DateTimeFormatter.ISO_INSTANT.parse("2023-06-19T01:41:45.341Z")).toString(),
                null,
                null
            )
        ).subscribeOn(Schedulers.io())

    fun updateTracking(tracking: Tracking, id: Int): io.reactivex.Observable<Tracking> =
        api.updateTracking(tracking, id).subscribeOn(Schedulers.io())

    fun deleteTracking(id: Int): io.reactivex.Observable<Tracking> =
        api.deleteTracking(id).subscribeOn(Schedulers.io())

}