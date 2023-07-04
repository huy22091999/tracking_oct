package com.oceantech.tracking.data.repository

import com.oceantech.tracking.data.model.TokenResponse
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.network.UserApi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.time.Year
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    val api: UserApi
) {
    fun getCurrentUser(): Observable<User> = api.getCurrentUser().subscribeOn(Schedulers.io())
    fun getString(): String = "test part"
    fun sign(
        user: User
    ):Observable<TokenResponse> = api.sign(user).subscribeOn(Schedulers.io())

    fun getAllUser():Observable<List<User>> = api.getAllUser().subscribeOn(Schedulers.io())
}