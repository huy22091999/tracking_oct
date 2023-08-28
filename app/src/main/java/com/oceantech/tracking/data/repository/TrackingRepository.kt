package com.oceantech.tracking.data.repository

import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.data.network.TrankingApi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingRepository @Inject constructor (
    private val api: TrankingApi
) {

    fun getAllTracking() : Observable<ArrayList<Tracking>> = api.getAllTracking().subscribeOn(Schedulers.io())

    fun addTracking(tracking: Tracking) : Observable<Tracking> = api.addTracking(tracking).subscribeOn(Schedulers.io())

    fun updateTracking(id : Int, tracking: Tracking) : Observable<Tracking> = api.updateTracking(id,tracking).subscribeOn(Schedulers.io())

    fun deleteTracking(id : Int) : Observable<Tracking> = api.deleteTracking(id).subscribeOn(Schedulers.io())

}