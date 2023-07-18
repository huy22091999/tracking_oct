package com.oceantech.tracking.data.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.observe
import com.google.firebase.messaging.FirebaseMessaging
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
    val api: UserApi,
    val context:Context
) {
    private val _tokenDevice:MutableLiveData<String> = MutableLiveData()
    val tokenDevice:LiveData<String>
        get() = _tokenDevice
    private val _hasTokenDevice:MutableLiveData<Boolean> = MutableLiveData()
    val hasTokenDevice:LiveData<Boolean>
        get() = _hasTokenDevice

    init {
        handleTokenDevice()
    }

    private fun handleTokenDevice(){
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener {
                _tokenDevice.value = it
                _hasTokenDevice.value = true
                Log.i("token's device repo:", it)
            }
            .addOnFailureListener {
                Log.e("error tokenDevice:", it.toString())
            }
    }
    fun blockUser(id:Int):Observable<User> = api.blockUser(id).subscribeOn(Schedulers.io())
    fun getCurrentUser(): Observable<User> = api.getCurrentUser().subscribeOn(Schedulers.io())
    fun edit(tokenDevice:String):Observable<User>{
        return api.edit(tokenDevice).subscribeOn(Schedulers.io())
    }
    fun getString(): String = "test part"
    fun sign(user: User):Observable<User> = api.sign(user).subscribeOn(Schedulers.io())
    fun getAllUser():Observable<List<User>> = api.getAllUser().subscribeOn(Schedulers.io())
    fun updateMyself(user: User):Observable<User> = api.updateMyself(user).subscribeOn(Schedulers.io())
    fun edit(id:Int, user: User):Observable<User> = api.edit(id,user).subscribeOn(Schedulers.io())
}