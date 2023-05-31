package com.oceantech.tracking.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class News(
    @SerializedName("categories")
    val categories: Category? = null,
    @SerializedName("content")
    val content: String? = null,
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("language")
    val language: Int? = null,
    @SerializedName("note")
    val note: String? = null,
    @SerializedName("publishDate")
    val publishDate: Date? = null,
    @SerializedName("realAuthor")
    val realAuthor: String? = null,
    @SerializedName("source")
    val source: String? = null,
    @SerializedName("status")
    val status: Int? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("titleImageUrl")
    val titleImageUrl: String? = null,
    @SerializedName("view")
    val view: Int? = null,
)

data class NewsFilter(
    @SerializedName("category")
    val category: Category? = null,
    @SerializedName("checkLanguage")
    val checkLanguage: Int? = null,
    @SerializedName("pageIndex")
    val pageIndex: Int? = null,
    @SerializedName("pageSize")
    val pageSize: Int? = null,
    @SerializedName("publishDate")
    val publishDate: Date? = null,
    @SerializedName("status")
    val status: Int? = null,
)
