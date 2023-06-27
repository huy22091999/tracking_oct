package com.oceantech.tracking.ui.tracking

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.Tracking

data class TrackingViewState(
    val saveTracking: Async<Tracking> = Uninitialized,
    val getAllTracking: Async<List<Tracking>> = Uninitialized,
    val deleteTracking: Async<Tracking> = Uninitialized,
    val updateTracking: Async<Tracking> = Uninitialized
): MavericksState {
}