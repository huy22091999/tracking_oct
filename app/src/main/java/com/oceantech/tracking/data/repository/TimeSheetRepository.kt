package com.oceantech.tracking.data.repository

import android.database.Observable
import com.oceantech.tracking.data.model.TimeSheet
import com.oceantech.tracking.data.network.TimeSheetApi
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimeSheetRepository @Inject constructor(
    private val api: TimeSheetApi
) {
    fun checkIn(ip: String): Flow<TimeSheet> = flow {
        emit(api.checkIn(ip))
    }.flowOn(Dispatchers.IO)

    fun getAllTimeSheets(): Flow<List<TimeSheet>> = flow {
        emit(api.getAllTimeSheets())
    }.flowOn(Dispatchers.IO)
}