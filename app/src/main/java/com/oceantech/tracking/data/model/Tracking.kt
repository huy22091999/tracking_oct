package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName

data class Tracking(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("date")
    val date : String? = null,
    @SerializedName("content")
    val content : String? = null,
    @SerializedName("user")
    val user: User? = null
)
