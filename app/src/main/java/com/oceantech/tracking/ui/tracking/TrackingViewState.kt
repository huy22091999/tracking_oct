package com.oceantech.tracking.ui.tracking

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.data.model.User
import java.util.Date

data class TrackingViewState(
    val isScrollDown : Boolean = true,

    val currentTime: Async<Long> = Uninitialized,
    val isRunning: Boolean = false,

    val getTrackings: Async<ArrayList<Tracking>> = Uninitialized,
    val addTracking: Async<Tracking> = Uninitialized,
    val updateTracking: Async<Tracking> = Uninitialized,
    val deleteTracking: Async<Tracking> = Uninitialized,
) : MvRxState {
//    fun isLoadding() = pageUsers is Loading || userCurrent is Loading
}