package com.oceantech.tracking.ui.tracking

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.Tracking

data class TrackingViewState(
    val listTracking: Async<List<Tracking>> = Uninitialized,
    val Tracking: Async<Tracking> = Uninitialized,
    val deleteTracking: Async<Tracking> = Uninitialized,
    val updateTracking: Async<Tracking> = Uninitialized
) : MvRxState {
    fun isLoadding() = listTracking is Loading ||
            Tracking is Loading ||
            deleteTracking is Loading ||
            updateTracking is Loading


}
