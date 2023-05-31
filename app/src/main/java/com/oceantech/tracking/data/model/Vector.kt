package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName

data class Vector(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("code")
    val code: String? = null,
    @SerializedName("familyName")
    val familyName: String? = null,
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("aedesAegyptiLarvae")
    val aedesAegyptiLarvae: Int? = null,
    @SerializedName("aedesAegyptiQty")
    val aedesAegyptiQty: Int? = null,
    @SerializedName("aedesAlbopictusLarvae")
    val aedesAlbopictusLarvae: Int? = null,
    @SerializedName("aedesAlbopictusQty")
    val aedesAlbopictusQty: Int? = null,
    @SerializedName("latitude")
    val latitude: Double? = null,
    @SerializedName("longitude")
    val longitude: Double? = null,
    @SerializedName("dengueLocation")
    val dengueLocation: DengueLocation? = null,
)
