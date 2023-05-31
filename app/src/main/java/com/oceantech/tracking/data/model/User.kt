package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class User(
    @SerializedName("id")
    val id: String? = null,
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
    val dob: Date? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("firstName")
    val firstName: String? = null,
    @SerializedName("oldPassword")
    val oldPassword: String? = null,
    @SerializedName("password")
    val password: String? = null,
    @SerializedName("setPassword")
    val setPassword: String? = null,
    @SerializedName("person")
    val person: Person? = null,
    @SerializedName("roles")
    val roles: List<Role>? = null
)
