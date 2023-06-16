package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class User(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("username")
    val username: String? = null,
    @SerializedName("active")
    val active: Boolean? = null,
    @SerializedName("birthPlace")
    val birthPlace: String? = null,
    @SerializedName("changePass")
    val changePass: Boolean? = null,
    @SerializedName("confirmPassword")
    val confirmPassword: String? = null,
    @SerializedName("displayName")
    val displayName: String? = null,

    @SerializedName("dob")
    val dob: String? = null,

    @SerializedName("email")
    val email: String? = null,
    @SerializedName("hasPhoto")
    val hasPhoto: Boolean = false,

    @SerializedName("firstName")
    val firstName: String? = null,
    @SerializedName("oldPassword")
    val oldPassword: String? = null,
    @SerializedName("password")
    val password: String? = null,
    @SerializedName("setPassword")
    val setPassword: String? = null,
    @SerializedName("roles")
    val roles: List<Role>? = null,
    @SerializedName("gender")
    val gender: String? = null,
    @SerializedName("lastName")
    val lastName: String? = null,
    @SerializedName("university")
    val university: String? = null,
    @SerializedName("year")
    val year: Int? = null



)
