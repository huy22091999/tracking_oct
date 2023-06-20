package com.oceantech.tracking.data.model

import java.util.Date

data class TimeSheet(
    val id : Int? = null,
    val ip : String? = null,
    val message : String? = null,
    val dateAttendance : Date? = null,
    val offline : Boolean? = null,
    val user : User? = null
)
