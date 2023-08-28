package com.oceantech.tracking.data.repository

import com.oceantech.tracking.data.model.TimeSheet
import com.oceantech.tracking.data.network.TimeSheetApi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimeSheetRepository @Inject constructor(private val api: TimeSheetApi) {

    fun getTimeSheet(): Observable<List<TimeSheet>> = api.getTimeSheet().subscribeOn(Schedulers.io())

    fun addCheckin(id : String): Observable<TimeSheet> = api.getTimeSheet(id).subscribeOn(Schedulers.io())
}