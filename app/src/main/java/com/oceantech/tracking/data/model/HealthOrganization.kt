package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName

data class HealthOrganization(

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("code")
    val code: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("address")
    val address: String? = null,


    ) {
    override fun toString(): String {
        return if (name.isNullOrEmpty()) super.toString() else name
    }
}

data class HealthOrganizationFilter(

    @SerializedName("checkLanguage")
    val checkLanguage: Int? = null,
    @SerializedName("pageIndex")
    val pageIndex: Int? = null,
    @SerializedName("pageSize")
    val pageSize: Int? = null,

    )