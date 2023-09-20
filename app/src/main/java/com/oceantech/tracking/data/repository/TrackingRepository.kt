package com.oceantech.tracking.data.repository

import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.data.network.TrackingApi
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import java.time.LocalDateTime
import javax.inject.Singleton

@Singleton
class TrackingRepository(val api: TrackingApi) {
    fun getTracking(): Observable<List<Tracking>> = api.getTracking().subscribeOn(Schedulers.io())

    fun updateTracking(id: Int, content: String): Observable<Tracking> = api.update(
        id, Tracking(
            content,
            LocalDateTime.now().toString(),
            id,
            null
        )
    ).subscribeOn(Schedulers.io())

    fun deleteTracking(id: Int): Observable<Tracking> = api.delete(id).subscribeOn(Schedulers.io())
    fun saveTracking(tracking: Tracking): Observable<Tracking> =
        api.saveTracking(tracking).subscribeOn(Schedulers.io())
}