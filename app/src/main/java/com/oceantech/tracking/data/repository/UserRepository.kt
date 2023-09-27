package com.oceantech.tracking.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.oceantech.tracking.data.model.UpLoadImage
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.network.UserApi
import com.oceantech.tracking.ui.users.UserPagingSource
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    fun edit(id: Int, user: User): Observable<User> =
        api.edit(id, user).subscribeOn(Schedulers.io())

    fun blockUser(id: Int): Observable<User> = api.blockUser(id).subscribeOn(Schedulers.io())

    fun upLoadFile(image: MultipartBody.Part): Observable<UpLoadImage> =
        api.uploadAttachment(image).subscribeOn(Schedulers.io())
}