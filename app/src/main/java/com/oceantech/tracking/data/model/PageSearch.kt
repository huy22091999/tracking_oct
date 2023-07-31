package com.oceantech.tracking.data.model

data class PageSearch(
    val keyWord: String? = null,
    val pageIndex: Int,
    val size: Int,
    val status: Int? = null
)