package com.oceantech.tracking.data.repository

import android.database.Observable
import com.oceantech.tracking.data.model.ConfigApp
import com.oceantech.tracking.data.network.PublicApi
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PublicRepository @Inject constructor(
    private val api: PublicApi
) {
    fun getConfigApp(): io.reactivex.Observable<ConfigApp> = api.getConfigApp().subscribeOn(Schedulers.io())
}