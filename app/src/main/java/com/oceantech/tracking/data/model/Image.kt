package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName
import java.io.InputStream

data class Image(
    @SerializedName("description")
    var description: String? = null,
    @SerializedName("filename")
    var filename: String? = null,
    @SerializedName("inputStream")
    var inputStream: InputStream? = null,
    @SerializedName("open")
    var open: Boolean? = null,
    @SerializedName("readable")
    var readable: Boolean? = null,
    @SerializedName("uri")
    var uri: String? = null,
    @SerializedName("url")
    var url: String? = null,
)

