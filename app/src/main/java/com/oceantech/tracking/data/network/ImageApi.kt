package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.Image
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface ImageApi {
    @GET("public/images/{name}")
    fun getImageFile(@Path("name") name:String):Observable<Image>
}