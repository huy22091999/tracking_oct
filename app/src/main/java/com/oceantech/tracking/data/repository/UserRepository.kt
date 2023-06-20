package com.oceantech.tracking.data.repository

import com.oceantech.tracking.data.model.TokenResponse
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.network.UserApi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
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
        displayName: String,
        firstName: String,
        lastName: String
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
            firstName,
            null,
            password,
            null,
            mutableListOf(),
            null,
            lastName,
            null,
            null
        )
    ).subscribeOn(Schedulers.io())

    fun getAllUsers(): Observable<List<User>> = api.getAllUsers().subscribeOn(Schedulers.io())
}