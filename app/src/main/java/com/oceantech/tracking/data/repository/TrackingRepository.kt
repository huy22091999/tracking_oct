package com.oceantech.tracking.data.repository


import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Constructor
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.data.network.TrackingApi
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingRepository @Inject constructor(
    val api:TrackingApi
    ) {
    fun getAllTrackingByUser() : Observable<List<Tracking>> = api.getAllTrackingByUser().subscribeOn(Schedulers.io())
    fun postNewTracking(tracking: Tracking): Observable<Tracking> = api.postNewTracking(tracking).subscribeOn(Schedulers.io())
    fun updateTracking(id:Int,tracking: Tracking):Observable<Tracking> =api.updateTracking(id,tracking).subscribeOn(Schedulers.io())
    fun deleteTracking(id:Int):Observable<Tracking> = api.deleteTracking(id).subscribeOn(Schedulers.io())
}