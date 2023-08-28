package com.oceantech.tracking.ui.users

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.Page
import com.oceantech.tracking.data.model.User

data class UserViewState(
    val asyncListUser:Async<Page<User>> = Uninitialized,
    val asyncBlockUser:Async<User> = Uninitialized,
) : MvRxState {
    fun isLoadding() = asyncListUser is Loading
}