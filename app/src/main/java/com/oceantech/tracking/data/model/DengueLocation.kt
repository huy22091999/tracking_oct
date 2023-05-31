package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class DengueLocation(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("province")
    val province: Province? = null,
    @SerializedName("district")
    val district: District? = null,
    @SerializedName("ward")
    val ward: Ward? = null,
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("investigationPerson")
    val investigationPerson: String? = null,
    @SerializedName("investigationDate")
    val investigationDate: Date? = null,
    @SerializedName("active")
    val active: Boolean? = null,
//"dengueLocationItems": [],
//"patientInformations": []
)
data class DengueLocationFilter(
    @SerializedName("pageIndex")
    val pageIndex: Int? = null,
    @SerializedName("pageSize")
    val pageSize: Int? = null,
)