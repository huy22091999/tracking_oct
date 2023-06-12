package com.oceantech.tracking.ui.trackings

import com.oceantech.tracking.core.NimpeViewModelAction
import com.oceantech.tracking.data.model.Tracking

sealed class TrackingViewAction : NimpeViewModelAction {
    object GetAllTracking : TrackingViewAction()
    data class Delete(val tracking: Tracking) : TrackingViewAction()
}