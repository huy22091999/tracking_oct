package com.oceantech.tracking.data.repository

import com.oceantech.tracking.data.model.TimeSheet
import com.oceantech.tracking.data.network.TimeSheetApi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TimeSheetRepository @Inject constructor(val api:TimeSheetApi){
    fun getAllByUser():Observable<List<TimeSheet>> = api.getAllByUser().subscribeOn(Schedulers.io())
    fun checkIn(ip:String):Observable<TimeSheet> = api.checkIn(ip).subscribeOn(Schedulers.io())
}