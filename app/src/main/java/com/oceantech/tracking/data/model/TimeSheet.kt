package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class TimeSheet(
    @SerializedName("dateAttendance")
    val dateAttendance: String? = null,
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("ip")
    val ip: String? = null,
    @SerializedName("message")
    val message:String ? = null,
    @SerializedName("offline")
    val offline:Boolean? = true,
    @SerializedName("user")
    val user: User? = null
)