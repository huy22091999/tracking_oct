package com.oceantech.tracking.data.repository

import android.annotation.SuppressLint
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.network.UserApi
import com.oceantech.tracking.ui.users.UserPagingSource
import com.oceantech.tracking.ui.users.UserPagingSource.Companion.NETWORK_PAGE_SIZE
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

    @SuppressLint("CheckResult")
    fun getUserByPage(): Flow<PagingData<User>> = Pager(
        config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
        pagingSourceFactory = { UserPagingSource(api) }
    ).flow

    fun updateUser(user: User): Observable<User> = api.updateUser(user).subscribeOn(Schedulers.io())
}