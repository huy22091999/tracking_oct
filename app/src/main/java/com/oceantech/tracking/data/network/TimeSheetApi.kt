package com.oceantech.tracking.data.network

import android.database.Observable
import com.oceantech.tracking.data.model.TimeSheet
import retrofit2.http.GET
import retrofit2.http.Query

interface TimeSheetApi {

    @GET("time-sheets/check-in")
    fun checkIn(@Query("ip") ip: String): io.reactivex.Observable<TimeSheet>

    @GET("time-sheets")
    fun getAllTimeSheets(): io.reactivex.Observable<List<TimeSheet>>
}