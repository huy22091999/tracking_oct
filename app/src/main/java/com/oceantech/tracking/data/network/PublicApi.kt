package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.Version
import io.reactivex.Observable
import retrofit2.http.GET

interface PublicApi {
    @GET("public/config-app")
    fun getConfigApp():Observable<Version>
}