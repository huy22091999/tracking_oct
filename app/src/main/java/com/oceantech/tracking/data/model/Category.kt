package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("code")
    val code: String? = null,
    @SerializedName("title")
    val title: String? = null,

    @SerializedName("titleImageUrl")
    val titleImageUrl: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("active")
    val active: Boolean? = null,
)

data class CategoryFilter(
    @SerializedName("checkLanguage")
    val checkLanguage: Int? = null,
    @SerializedName("pageIndex")
    val pageIndex: Int? = null,
    @SerializedName("pageSize")
    val pageSize: Int? = null,
)