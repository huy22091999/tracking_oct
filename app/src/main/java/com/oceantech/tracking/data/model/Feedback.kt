package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName

data class Feedback(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("user")
    val user: User? = null,
    @SerializedName("feedback")
    val feedback: String? = null,
)
