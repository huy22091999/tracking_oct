package com.oceantech.tracking.data.repository

import android.util.Log
import com.oceantech.tracking.data.model.TokenResponse
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.network.UserApi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    val api: UserApi
) {
    fun getCurrentUser(): Observable<User> = api.getCurrentUser().subscribeOn(Schedulers.io())
    fun getString(): String = "test part"

    fun createUpdateUser(
        userName: String,
        password: String,
        displayName: String
    ): Observable<TokenResponse> = api.createUpdateUser(
        User(
            null,
            userName,
            true,
            null,
            false,
            null,
            displayName,
            null,
            null,
            false,
            null,
            null,
            password,
            null,
            mutableListOf(),
            null,
            null,
            null,
            null
        )
    ).subscribeOn(Schedulers.io())
}