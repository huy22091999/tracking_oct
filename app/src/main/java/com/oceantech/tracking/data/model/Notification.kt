package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName

data class Notification(
    @SerializedName("body")
    val body: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("user")
    val user: User? = null
)