package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Role(
    @SerializedName("id")
    val id:String?=null,
    @SerializedName("name")
    val name:String?=null,

    @SerializedName("authority")
    val authority:String?=null,

    ): Serializable
