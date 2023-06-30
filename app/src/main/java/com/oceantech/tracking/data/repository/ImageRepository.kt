package com.oceantech.tracking.data.repository

import android.content.Context
import com.oceantech.tracking.data.model.Image
import com.oceantech.tracking.data.network.ImageApi
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ImageRepository(
    val api:ImageApi
) {
    fun getImageFile(name:String):Observable<Image> = api.getImageFile(name).subscribeOn(Schedulers.io())
}