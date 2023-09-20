package com.oceantech.tracking.ui.profile

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.TokenResponse
import com.oceantech.tracking.data.model.User

data class InfoViewsState(
    val isMyProfile : Boolean = false,
    val isAdmin : Boolean = false,
    val myUserUser : Async<User>  = Uninitialized,
    val userEdit : Async<User>  = Uninitialized,
    val updateUser : Async<User>  = Uninitialized,
    val userVerify : Async<TokenResponse>  = Uninitialized
) : MvRxState {
    fun isLoadding() = userEdit is Loading
}