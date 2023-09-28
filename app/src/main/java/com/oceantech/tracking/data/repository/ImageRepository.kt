package com.oceantech.tracking.data.repository

import com.oceantech.tracking.data.model.UpLoadImage
import com.oceantech.tracking.data.network.ImageApi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import javax.inject.Singleton

@Singleton
class ImageRepository(
    val api: ImageApi
) {
//    fun upLoadFile(image: MultipartBody.Part): Observable<UpLoadImage> =
//        api.uploadAttachment(image).subscribeOn(Schedulers.io())
}