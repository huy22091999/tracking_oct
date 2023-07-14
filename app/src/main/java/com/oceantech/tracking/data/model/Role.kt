package com.oceantech.tracking.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.NonDisposableHandle.parent
import kotlinx.parcelize.Parcelize

data class Role(
    @SerializedName("id")
    val id:Int?=null,
    @SerializedName("name")
    val name:String?=null,

    @SerializedName("authority")
    val authority:String?=null,
    @SerializedName("description")
    val description: String? = null
)
