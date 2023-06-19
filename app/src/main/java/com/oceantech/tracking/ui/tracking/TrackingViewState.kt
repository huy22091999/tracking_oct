package com.oceantech.tracking.ui.tracking

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.Tracking

data class TrackingViewState(
    var saveTracking: Async<Tracking> = Uninitialized,
    var getAllTracking: Async<List<Tracking>> = Uninitialized,
): MvRxState {
}