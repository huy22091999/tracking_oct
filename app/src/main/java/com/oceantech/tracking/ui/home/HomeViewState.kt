package com.oceantech.tracking.ui.home

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.*

data class HomeViewState(
    val userCurrent:Async<User> = Uninitialized,

) : MvRxState {
    fun isLoadding() =
            userCurrent is Loading

}