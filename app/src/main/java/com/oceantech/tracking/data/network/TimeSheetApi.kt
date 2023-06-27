package com.oceantech.tracking.data.network

import android.database.Observable
import com.oceantech.tracking.data.model.TimeSheet
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Query

interface TimeSheetApi {

    @GET("time-sheets/check-in")
    suspend fun checkIn(@Query("ip") ip: String): TimeSheet

    @GET("time-sheets")
    suspend fun getAllTimeSheets(): List<TimeSheet>
}