package com.oceantech.tracking.data.repository

import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.network.SignApi
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SignRepository @Inject constructor(
    val api : SignApi
) {
    fun signIn(user: User) : io.reactivex.Observable<User> = api.signIn(user).subscribeOn(Schedulers.io())
}