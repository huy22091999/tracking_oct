package com.oceantech.tracking.ui.profile

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.User

data class ProfileViewState(
    val userCurrent: Async<User> = Uninitialized
) : MvRxState {
    fun isLoadding() =
        userCurrent is Loading
}