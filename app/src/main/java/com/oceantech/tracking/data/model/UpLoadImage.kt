package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UpLoadImage(
    @SerializedName("contentSize") val contentSize: Int? = null,
    @SerializedName("contentType") val contentType: String? = null,
    @SerializedName("extension") val extension: String? = null,
    @SerializedName("filePath") var filePath: String? = null,
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") var name: String? = null

) : Serializable
