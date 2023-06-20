package com.oceantech.tracking.ui.security

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.TokenResponse
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.model.Version

data class SecurityViewState (
    var asyncLogin: Async<TokenResponse> = Uninitialized,
    var asyncSignIn : Async<User> = Uninitialized,
    var userCurrent:Async<User> = Uninitialized,
    var asyncVersion : Async<Version> = Uninitialized
    ):MvRxState{
        fun isLoading()= asyncLogin is Loading
    }