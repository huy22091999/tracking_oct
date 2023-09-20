package com.oceantech.tracking.ui.tracking

import com.oceantech.tracking.core.NimpeViewModelAction
import com.oceantech.tracking.data.model.Tracking

sealed class TrackingViewAction : NimpeViewModelAction {

    object getTrackingAction : TrackingViewAction()
    data class saveTracking(val tracking: Tracking) : TrackingViewAction()
    data class deleteTracking(val id: Int) : TrackingViewAction()

}
