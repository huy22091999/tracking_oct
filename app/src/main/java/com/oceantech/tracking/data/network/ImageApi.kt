package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.UpLoadImage
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface ImageApi {
    @Multipart
    @POST("public/uploadFile")
    fun uploadAttachment(
        @Part file: MultipartBody.Part
    ): retrofit2.Call<UpLoadImage>

    @Multipart
    @POST("public/uploadFile")
    fun uploadFile(@Part file: MultipartBody.Part): retrofit2.Call<UpLoadImage>
}