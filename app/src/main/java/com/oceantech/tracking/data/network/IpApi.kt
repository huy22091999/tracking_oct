package com.oceantech.tracking.data.network

import io.reactivex.Observable
import retrofit2.http.GET

interface IpApi {
    @GET("/")
    fun getIp() : Observable<String>
}