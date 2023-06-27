package com.oceantech.tracking.data.repository

import com.oceantech.tracking.data.model.TokenResponse
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.network.UserApi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    val api: UserApi
) {
    fun getCurrentUser(): Flow<User> = flow {
        emit(api.getCurrentUser())
    }.flowOn(Dispatchers.IO)

    fun getString(): String = "test part"

    fun createUpdateUser(
        userName: String,
        password: String,
        displayName: String,
        firstName: String,
        lastName: String
    ): Flow<TokenResponse> = flow {
        emit(
            api.createUpdateUser(
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
            )
        )
    }.flowOn(Dispatchers.IO)

    fun getAllUsers(): Flow<List<User>> = flow {
        emit(api.getAllUsers())
    }.flowOn(Dispatchers.IO)
}