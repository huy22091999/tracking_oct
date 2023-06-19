package com.oceantech.tracking.data.model

data class TimeSheet(
    var dateAttendance: String? = null,
    val id: Int? = null,
    val ip: String? = null,
    val note: String? = null,
    val user: User? = null
)