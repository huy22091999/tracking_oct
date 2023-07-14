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
        lastName: String,
        gender: String,
        dob: String,
        email: String,
        university: String,
        year: Int,
        confirmPassword: String
    ): Flow<User> = flow {
        emit(
            api.createUpdateUser(
                User(
                    null,
                    userName,
                    true,
                    null,
                    false,
                    confirmPassword,
                    displayName,
                    dob,
                    email,
                    false,
                    firstName,
                    null,
                    password,
                    null,
                    mutableListOf(),
                    gender,
                    lastName,
                    university,
                    year
                )
            )
        )
    }.flowOn(Dispatchers.Main)

    fun getAllUsers(): Flow<List<User>> = flow {
        emit(api.getAllUsers())
    }.flowOn(Dispatchers.IO)

    fun getDevice(tokenDevice: String) : Flow<User> = flow {
        emit(
            api.getDevice(tokenDevice)
        )
    }.flowOn(Dispatchers.IO)

    fun getBlockUser(id: Int): Flow<User> = flow {
        emit(
            api.getBlockUser(id)
        )
    }.flowOn(Dispatchers.IO)

    fun updateMyself(user: User): Flow<User> = flow {
        emit(
            api.updateMyself(user)
        )
    }.flowOn(Dispatchers.IO)

    fun updateUser(user: User, id : Int): Flow<User> = flow {
        emit(
            api.updateUser(user, id)
        )
    }.flowOn(Dispatchers.IO)
}