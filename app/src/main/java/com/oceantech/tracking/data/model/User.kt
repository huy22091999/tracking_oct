package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class User(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("username")
    var username: String? = null,
    @SerializedName("active")
    val active: Boolean? = null,
    @SerializedName("birthPlace")
    var birthPlace: String? = null,
    @SerializedName("changePass")
    var changePass: Boolean? = null,
    @SerializedName("confirmPassword")
    var confirmPassword: String? = null,
    @SerializedName("countDayCheckin")
    val countDayCheckin:Int,
    @SerializedName("countDayTracking")
    val countDayTracking:Int,
    @SerializedName("displayName")
    var displayName: String? = null,
    @SerializedName("dob")
    val dob: Date? = null,
    @SerializedName("email")
    var email: String? = null,
    @SerializedName("firstName")
    var firstName: String? = null,
    @SerializedName("gender")
    var gender:String? = null,
    @SerializedName("hasPhoto")
    var hasPhoto:Boolean? = null,
    @SerializedName("lastName")
    var lastName:String? = null,
    @SerializedName("password")
    var password: String? = null,
    @SerializedName("setPassword")
    var setPassword: String? = null,
    @SerializedName("roles")
    var roles: List<Role>? = null,
    @SerializedName("tokenDevice")
    var tokenDevice:String? = null,
    @SerializedName("university")
    var university: String? = null,
    @SerializedName("year")
    var year:Int? = null
):Serializable

data class UserFilter(
    @SerializedName("pageIndex")
    var pageIndex:Int? = null,
    @SerializedName("size")
    var size:Int? = null,
):Serializable
