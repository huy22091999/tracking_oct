package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName

data class Version(
    @SerializedName("versionName")
    var versionName:String?=null
)
