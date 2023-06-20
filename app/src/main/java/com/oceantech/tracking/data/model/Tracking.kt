package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Tracking(
    @SerializedName("content")
    val content: String? = null,
    @SerializedName("date")
    val date: String? = null,
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("user")
    val user: User? = null
)