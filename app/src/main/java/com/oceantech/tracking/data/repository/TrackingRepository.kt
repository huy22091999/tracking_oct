package com.oceantech.tracking.ui.home.repository

import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.data.network.TrackingApi
import com.oceantech.tracking.utils.getCurrentDateTime
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.time.LocalDateTime
import javax.inject.Singleton
import javax.inject.Inject

@Singleton
class TrackingRepository @Inject constructor(
    val api:TrackingApi
) {
    fun save(content:String): Observable<Tracking> = api.save(Tracking(
        content,
        LocalDateTime.now().toString(),
        null,
        null
    )).subscribeOn(Schedulers.io())

    fun update(id:Int, content:String): Observable<Tracking> = api.update(Tracking(
        content,
        null,
        id,
        null
    )).subscribeOn(Schedulers.io())

    fun delete(id:Int):Observable<Tracking> = api.delete(id).subscribeOn(Schedulers.io())

    fun getAllByUser():Observable<List<Tracking>> = api.getAllByUser().subscribeOn(Schedulers.io())
}