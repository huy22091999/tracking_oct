package com.oceantech.tracking.data.network

import android.database.Observable
import com.oceantech.tracking.data.model.ConfigApp
import retrofit2.http.GET

interface PublicApi {
    @GET("public/config-app")
    fun getConfigApp(): io.reactivex.Observable<ConfigApp>
}