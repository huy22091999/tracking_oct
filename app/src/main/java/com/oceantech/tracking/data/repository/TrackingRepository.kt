package com.oceantech.tracking.data.repository

import android.database.Observable
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.network.TrackingApi
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingRepository @Inject constructor(
    private val api: TrackingApi
) {

    fun getAllTracking(): io.reactivex.Observable<List<Tracking>> =
        api.getAllTracking().subscribeOn(Schedulers.io())

    fun saveTracking(firstName: String, lastName: String, dob: String, gender: String): io.reactivex.Observable<Tracking> =
        api.saveTracking(
            Tracking(
            null,
            null,
            null,
            User(
                active = true,
                birthPlace = null,
                changePass = null,
                confirmPassword = null,
                displayName = null,
                dob = dob,
                email = null,
                firstName = firstName,
                gender = gender,
                hasPhoto = false,
                id = null,
                lastName = lastName,
                password = null,
                roles = mutableListOf(),
                setPassword = null,
                university = null,
                username = null,
                year = null
            )
        )
        ).subscribeOn(Schedulers.io())

    fun updateTracking(id: Int): io.reactivex.Observable<Tracking> =
        api.updateTracking(id).subscribeOn(Schedulers.io())

    fun deleteTracking(id: Int): io.reactivex.Observable<Tracking> = api.deleteTracking(id).subscribeOn(Schedulers.io())

}