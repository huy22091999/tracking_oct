package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName

data class Role(
    @SerializedName("id")
    val id:Long?=null,
    @SerializedName("name")
    val name:String?=null,
    @SerializedName("authority")
    val authority:String?=null,

    )
