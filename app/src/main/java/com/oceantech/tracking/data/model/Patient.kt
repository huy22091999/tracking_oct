package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName

data class Patient(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("code")
    val code: String? = null,
    @SerializedName("name")
    val familyName: String? = null,
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("latitude")
    val latitude: Double? = null,
    @SerializedName("longitude")
    val longitude: Double? = null,
    @SerializedName("dengueLocation")
    val dengueLocation: DengueLocation? = null,
)
