package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName

data class District(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("code")
    val code: String? = null,
    @SerializedName("level")
    val level: Int? = null,
    @SerializedName("longitude")
    val longitude:Double? = null,
    @SerializedName("latitude")
    val latitude:Double? = null,
)

//"parent": null,
//"subAdministrativeUnits": null,
//"mapCode": null,
//"gMapX": null,
//"gMapY": null,
//"totalAcreage": null
