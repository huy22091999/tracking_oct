package com.oceantech.tracking.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.oceantech.tracking.data.model.Page
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.model.UserFilter
import com.oceantech.tracking.data.network.UserApi
import com.oceantech.tracking.data.network.UsersPagingSource
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    val api: UserApi
) {
    fun getCurrentUser(): Observable<User> = api.getCurrentUser().subscribeOn(Schedulers.io())

    fun getAllUser(): Flow<PagingData<User>> {
        return Pager(
            PagingConfig(
                pageSize = 10,
                prefetchDistance = 5,
                enablePlaceholders = false
            )
        ){
            UsersPagingSource(api)
        }.flow
    }

    fun updateMyself(user: User):Observable<User> =api.updateMyself(user).subscribeOn(Schedulers.io())

    fun getString(): String = "test part"
}