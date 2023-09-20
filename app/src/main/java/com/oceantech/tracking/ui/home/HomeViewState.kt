package com.oceantech.tracking.ui.home

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.*

data class HomeViewState(
    val itemTabLayout:Async<List<ItemTab>> = Uninitialized,
    val userCurrent:Async<User> = Uninitialized,
    val isMode: Boolean = false,
    val language: String = "",

) : MvRxState {
    fun isLoadding() =
            userCurrent is Loading

}