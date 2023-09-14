package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName

data class Page<T>(

    @SerializedName("content")
    val content: List<T>? = ArrayList(),

    @SerializedName("totalPages")
    val totalPages: Int? = null,

    @SerializedName("totalElements")
    val totalElements: Int? = null,

    @SerializedName("number")
    val number: Int? = null,

    @SerializedName("size")
    val size: Int? = null,

    @SerializedName("first")
    val first: Boolean? = null,

    @SerializedName("empty")
    val empty: Boolean? = null

)

data class Pageable(

    @SerializedName("pageIndex")
    val pageNumber: Int? = null,

    @SerializedName("size")
    val pageSize: Int? = null

)
