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
    var active: Boolean? = null,
    @SerializedName("birthPlace")
    var birthPlace: String? = null,
    @SerializedName("changePass")
    var changePass: Boolean? = null,
    @SerializedName("confirmPassword")
    var confirmPassword: String? = null,
    @SerializedName("displayName")
    var displayName: String? = null,

    @SerializedName("dob")
    val dob: Date? = null,

    @SerializedName("email")
    var email: String? = null,

    @SerializedName("firstName")
    val firstName: String? = null,

    @SerializedName("lastName")
    val lastName: String? = null,
//    @SerializedName("oldPassword")
//    val oldPassword: String? = null,
    @SerializedName("password")
    var password: String? = null,
    @SerializedName("setPassword")
    val setPassword: Boolean? = null,
//    @SerializedName("person")
//    val person: Person? = null,
    @SerializedName("roles")
    val roles: List<Role>? = null,
    @SerializedName("countDayCheckin")
    val countDayCheckin: Int? = null,
    @SerializedName("countDayTracking")
    val countDayTracking: Int? = null,
    @SerializedName("gender")
    var gender: String? = null,
    @SerializedName("hasPhoto")
    val hasPhoto: Boolean? = null,
    @SerializedName("tokenDevice")
    val tokenDevice: String? = null,
    @SerializedName("university")
    var university: String? = null,
    @SerializedName("year")
    val year: Int? = null,
    @SerializedName("image")
    var image: String? = null
) : Serializable
