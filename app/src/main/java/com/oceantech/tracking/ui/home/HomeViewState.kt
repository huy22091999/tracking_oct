package com.oceantech.tracking.ui.home

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.*

data class HomeViewState(
    val userCurrent: Async<User> = Uninitialized,
    val allUsers: Async<List<User>> = Uninitialized,
    val device: Async<User> = Uninitialized,
    val lockUser: Async<User> = Uninitialized,
    val updateMyself: Async<User> = Uninitialized,
    val updateUser: Async<User> = Uninitialized,
    val getUser: Async<User> = Uninitialized
) : MavericksState {
    fun isLoading() = userCurrent is Loading || allUsers is Loading || device is Loading
            || lockUser is Loading || updateMyself is Loading || updateUser is Loading || getUser is Loading

}