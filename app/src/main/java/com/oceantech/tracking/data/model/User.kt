package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class User(
    var active: Boolean,
    var birthPlace: String,
    val changePass: Boolean,
    var confirmPassword: String,
    val countDayCheckin: Int,
    val countDayTracking: Int,
    var displayName: String,
    val dob: String,
    var email: String,
    var firstName: String,
    var gender: String,
    val hasPhoto: Boolean,
    var id: Int? =null,
    var lastName: String,
    var password: String,
    val roles: List<Role>,
    val setPassword: Boolean,
    val tokenDevice: String,
    var university: String,
    var username: String,
    var year: Int
):Serializable

data class UserFilter(
    @SerializedName("pageIndex")
    var pageIndex:Int? = null,
    @SerializedName("size")
    var size:Int? = null,
): Serializable