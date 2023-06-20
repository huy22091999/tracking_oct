package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.TimeSheet
import com.oceantech.tracking.data.model.User
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query

interface TimeSheetApi {
    @GET("time-sheets/")
    fun getAllByUser():Observable<List<TimeSheet>>
    @GET("time-sheets/check-in")
    fun checkIn(@Query("ip") ip:String):Observable<TimeSheet>
}