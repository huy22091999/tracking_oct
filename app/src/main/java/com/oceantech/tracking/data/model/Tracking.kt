package com.oceantech.tracking.data.model

data class Tracking(
    val id: Int? = null,
    var content: String? = null,
    val date: String? = null,
    val user: User? = null
)