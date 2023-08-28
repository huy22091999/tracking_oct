package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.TimeSheet
import io.reactivex.Observable
import io.reactivex.Observer
import retrofit2.http.GET
import retrofit2.http.Query

interface TimeSheetApi {
    //mita/time-sheets
    @GET("time-sheets")
    fun getAllByUser():Observable<List<TimeSheet>>
    @GET("time-sheets/check-in")
    fun checkIn(@Query("ip") ip: String):Observable<TimeSheet>
}