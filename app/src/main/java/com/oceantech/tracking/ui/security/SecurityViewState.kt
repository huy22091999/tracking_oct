package com.oceantech.tracking.ui.security

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.TokenResponse
import com.oceantech.tracking.data.model.User

data class SecurityViewState(
    val asyncLogin: Async<TokenResponse> = Uninitialized,
    val userCurrent: Async<User> = Uninitialized,
    val userSignIn: Async<User> = Uninitialized,

) : MavericksState {
    fun isLoading() = asyncLogin is Loading
}