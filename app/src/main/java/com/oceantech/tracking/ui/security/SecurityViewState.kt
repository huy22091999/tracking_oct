package com.oceantech.tracking.ui.security

import com.airbnb.mvrx.Async
import com.oceantech.tracking.data.model.User
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.TokenResponse
//done
data class SecurityViewState (
    var asyncLogin: Async<TokenResponse> = Uninitialized,
    var userCurrent:Async<User> = Uninitialized
    ):MvRxState