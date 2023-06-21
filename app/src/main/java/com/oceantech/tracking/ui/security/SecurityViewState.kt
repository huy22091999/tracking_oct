package com.oceantech.tracking.ui.security

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.TokenResponse
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.model.Version

data class SecurityViewState (
    var asyncLogin: Async<TokenResponse> = Uninitialized,
    var asyncSign:Async<TokenResponse> = Uninitialized,
    var userCurrent:Async<User> = Uninitialized,
    val asyncSaveTracking:Async<Tracking> = Uninitialized,
    val asyncConfigApp:Async<Version> = Uninitialized
    ):MvRxState{
        fun isLoading()= asyncLogin is Loading
                || asyncSign is Loading
                || userCurrent is Loading
                || asyncSaveTracking is Loading
    }