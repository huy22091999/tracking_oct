package com.oceantech.tracking.data.repository

import com.oceantech.tracking.data.model.Version
import com.oceantech.tracking.data.network.PublicApi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class PublicRepository @Inject constructor(val api:PublicApi) {
    fun getConfigApp(): Observable<Version> = api.getConfigApp().subscribeOn(Schedulers.io())
}