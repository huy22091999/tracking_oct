package com.oceantech.tracking.ui.home.repository

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
        username:String,displayName:String, email:String,firstName:String,lastName:String, password:String, birthPlace:String, university:String, year: Int,
    ):Observable<TokenResponse> = api.sign(User(
        null,
        username,
        true,
        birthPlace,
        false,
        password,
        displayName,
        null,
        email,
        firstName,
        null,
        false,
        lastName,
        password,
        null,
        mutableListOf(),
        university,
        year
    )).subscribeOn(Schedulers.io())

    fun getAllUser():Observable<List<User>> = api.getAllUser().subscribeOn(Schedulers.io())
}