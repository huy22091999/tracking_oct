package com.oceantech.tracking.ui.tracking

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.Tracking

data class TrackingViewState(
    val listTracking: Async<List<Tracking>> = Uninitialized,
    val Tracking: Async<Tracking> = Uninitialized,
    val deleteTracking:Async<Tracking> = Uninitialized
) : MvRxState {

}
