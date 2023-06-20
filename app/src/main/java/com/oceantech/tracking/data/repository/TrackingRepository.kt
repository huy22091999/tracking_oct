package com.oceantech.tracking.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.oceantech.tracking.data.model.Page
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.data.network.TrackingApi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Singleton
import javax.inject.Inject

@Singleton
class TrackingRepository @Inject constructor(
    val api:TrackingApi
) {
    fun save(content:String): Observable<Tracking> = api.save(Tracking(
        content,
        LocalDateTime.now().toString(),
        null,
        null
    )).subscribeOn(Schedulers.io())

    fun update(id:Int, content:String): Observable<Tracking> = api.update(id,Tracking(
        content,
        LocalDateTime.now().toString(),
        id,
        null
    )).subscribeOn(Schedulers.io())

    fun delete(id:Int):Observable<Tracking> = api.delete(id).subscribeOn(Schedulers.io())

    fun getAllByUser():Observable<List<Tracking>> = api.getAllByUser().subscribeOn(Schedulers.io())

//    fun getAllByUser():Flow<PagingData<Tracking>> {
//        return Pager(
//            PagingConfig(
//                enablePlaceholders = false,
//                pageSize = 10,
//                prefetchDistance = 5
//            )
//        ){
//            TrackingPagingSource(api)
//        }.flow
//    }
}