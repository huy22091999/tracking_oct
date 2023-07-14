package com.oceantech.tracking.ui.home

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.*

data class HomeViewState(
    val userCurrent:Async<User> = Uninitialized,
    val asyncSaveTracking:Async<Tracking> = Uninitialized,
    val asyncUpdateTracking:Async<Tracking> = Uninitialized,
    val asyncDeleteTracking:Async<Tracking> = Uninitialized,
    val allTracking: Async<List<Tracking>> = Uninitialized,
    val timeSheets:Async<List<TimeSheet>> = Uninitialized,
    val checkIn:Async<TimeSheet> = Uninitialized,
    val allUsers:Async<List<User>> = Uninitialized,
    val asyncBlockUser:Async<User> = Uninitialized,
    val asyncTokenDevice:Async<User> = Uninitialized,
    val asyncUpdateMySelf:Async<User> = Uninitialized,
    val asyncEditUser:Async<User> = Uninitialized,
    val asyncLogout:Async<Unit> = Uninitialized

) : MvRxState {
    fun isLoadding() =
        userCurrent is Loading ||
                asyncSaveTracking is Loading ||
                asyncUpdateTracking is Loading ||
                asyncDeleteTracking is Loading ||
                allTracking is Loading ||
                timeSheets is Loading ||
                checkIn is Loading ||
                allUsers is Loading ||
                asyncBlockUser is Loading  ||
                asyncTokenDevice is Loading ||
                asyncUpdateMySelf is Loading ||
                asyncEditUser is Loading
}