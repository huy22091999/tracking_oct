package com.oceantech.tracking.data.repository

import com.oceantech.tracking.data.model.TimeSheet
import com.oceantech.tracking.data.network.IpApi
import com.oceantech.tracking.data.network.TimeSheetApi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimeSheetRepository @Inject constructor(val api : TimeSheetApi, private val ipApi: IpApi) {

    fun checkIn(ip: String?): Observable<TimeSheet> = api.checkIn(ip).subscribeOn(Schedulers.io())
    fun getTimeSheet() : Observable<List<TimeSheet>> = api.getTimeSheet().subscribeOn(Schedulers.io())
    fun getIp() : Observable<String> = ipApi.getIp().subscribeOn(Schedulers.io())
}