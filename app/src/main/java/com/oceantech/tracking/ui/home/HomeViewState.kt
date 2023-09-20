package com.oceantech.tracking.ui.home

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.*
//done
data class HomeViewState(
    val userCurrent: Async<User> = Uninitialized,
    val updateCurrent: Async<User> =Uninitialized,
    val timeSheets: Async<List<TimeSheet>> = Uninitialized,
    val timeSheet: Async<TimeSheet> = Uninitialized
) : MvRxState {
    fun isLoading() = userCurrent is Loading || timeSheets is Loading || timeSheet is Loading
}