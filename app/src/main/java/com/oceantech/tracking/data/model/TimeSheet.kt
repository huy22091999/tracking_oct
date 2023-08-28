package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName

data class TimeSheet(
    val id: Int? = null,
    val dateAttendance: String? = null,
    val message: String? = null,
    val ip: String? = null,
    val user: User? = null,
    val offline: Boolean? = null,
) {

}