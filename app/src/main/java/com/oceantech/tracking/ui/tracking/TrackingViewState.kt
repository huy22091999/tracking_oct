package com.oceantech.tracking.ui.tracking

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.Tracking
//done
data class TrackingViewState(
    val asyncTrackingArray: Async<List<Tracking>> = Uninitialized,
    val asyncSaveTracking: Async<Tracking> = Uninitialized,
    val asyncUpdateTracking: Async<Tracking> =Uninitialized,
    val asyncDeleteTracking: Async<Tracking> = Uninitialized
    ):MvRxState