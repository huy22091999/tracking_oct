package com.oceantech.tracking.ui.trackings

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.Tracking

data class TrackingViewState(
    var asyncListTracking : Async<MutableList<Tracking>> = Uninitialized,
    var asyncDelete : Async<Tracking> = Uninitialized,
    var asyncUpdate : Async<Tracking> = Uninitialized,
    var asyncTracking : Async<Tracking> = Uninitialized,
) : MvRxState