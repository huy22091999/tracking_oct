package com.oceantech.tracking.data.model

data class Notification(
    val body: String? = null,
    val date: String? = null,
    val id: Int? = null,
    val title: String? = null,
    val type: String? = null,
    val user: User? = null
)
