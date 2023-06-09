package com.oceantech.tracking.data.repository

import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.data.network.TrackingApi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingRepository @Inject constructor(val api: TrackingApi) {
    fun tracking(tracking: Tracking): Observable<Tracking> =
        api.tracking(tracking).subscribeOn(Schedulers.io())
}