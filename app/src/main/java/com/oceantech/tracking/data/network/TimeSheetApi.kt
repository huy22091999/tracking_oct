package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.TimeSheet
import io.reactivex.Observable
import retrofit2.http.GET

interface TimeSheetApi {
    @GET("time-sheets")
    fun getAllByUser():Observable<List<TimeSheet>>
    @GET("time-sheets/check-in")
    fun checkIn():Observable<TimeSheet>
}