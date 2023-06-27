package com.oceantech.tracking.data.repository

import android.database.Observable
import com.oceantech.tracking.data.model.ConfigApp
import com.oceantech.tracking.data.network.PublicApi
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.subscribeOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PublicRepository @Inject constructor(
    private val api: PublicApi
) {
    fun getConfigApp(): Flow<ConfigApp> = flow {
        emit(api.getConfigApp())
    }.flowOn(Dispatchers.IO)
}