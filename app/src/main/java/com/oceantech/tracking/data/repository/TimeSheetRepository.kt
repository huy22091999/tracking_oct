package com.oceantech.tracking.data.repository

import android.database.Observable
import com.oceantech.tracking.data.model.TimeSheet
import com.oceantech.tracking.data.network.TimeSheetApi
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimeSheetRepository @Inject constructor(
    private val api: TimeSheetApi
) {
    fun checkIn(ip: String): io.reactivex.Observable<TimeSheet> = api.checkIn(ip).subscribeOn(Schedulers.io())

    fun getAllTimeSheets(): io.reactivex.Observable<List<TimeSheet>> = api.getAllTimeSheets().subscribeOn(Schedulers.io())
}