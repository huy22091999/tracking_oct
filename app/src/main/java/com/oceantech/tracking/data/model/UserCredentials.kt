package com.oceantech.tracking.data.model

import com.oceantech.tracking.data.network.AuthApi
import com.google.gson.annotations.SerializedName

data class UserCredentials @JvmOverloads constructor(

    @SerializedName("client_id")
    val clientId: String = AuthApi.CLIENT_ID,

    @SerializedName("client_secret")
    val clientSecret: String = AuthApi.CLIENT_SECRET,

    @SerializedName("username")
    val username: String? = null,

    @SerializedName("password")
    val password: String? = null,

    @SerializedName("refresh_token")
    val refreshToken: String? = null,

    @SerializedName("grant_type")
    val grantType: String,
)