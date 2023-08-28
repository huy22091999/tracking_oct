package com.oceantech.tracking.data.model

data class TimeSheet(
    val dateAttendance: String,
    val id: Int,
    val ip: String,
    val message: String,
    val offline: Boolean,
    val user: User
)