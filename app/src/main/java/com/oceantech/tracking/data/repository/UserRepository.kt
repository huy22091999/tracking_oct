package com.oceantech.tracking.data.repository

import android.annotation.SuppressLint
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.network.UserApi
import com.oceantech.tracking.ui.users.UserPagingSource
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
class UserRepository(
    val api: UserApi
) {
    fun getCurrentUser(): Observable<User> = api.getCurrentUser().subscribeOn(Schedulers.io())
    fun getString(): String = "test part"

    fun updateUser(user: User): Observable<User> = api.updateUser(user).subscribeOn(Schedulers.io())

    fun getUserByPage(): Flow<PagingData<User>> = Pager(
        config = PagingConfig(
            pageSize = UserPagingSource.NETWORK_PAGE_SIZE,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { UserPagingSource(api) }
    ).flow
}