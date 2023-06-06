package com.oceantech.tracking.data.model

import java.util.Date

data class TimeSheet(
    val dateAttendance: Date? = null,
    val id: Int? = null,
    val ip: String? = null,
    val note: String? = null,
    val user: User? = null
)