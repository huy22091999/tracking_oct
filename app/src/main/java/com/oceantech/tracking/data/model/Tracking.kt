package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Tracking(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("content")
    val content : String? = null,
    @SerializedName("date")
    val date : Date? = null
)
