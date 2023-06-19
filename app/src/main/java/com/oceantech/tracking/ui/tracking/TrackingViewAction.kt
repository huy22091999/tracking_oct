package com.oceantech.tracking.ui.tracking

import com.oceantech.tracking.core.NimpeViewModelAction
import com.oceantech.tracking.data.model.Tracking

open class TrackingViewAction : NimpeViewModelAction {

    class GetAllTracking() : TrackingViewAction()
    data class SaveTracking(val content: String) : TrackingViewAction()

    data class DeleteTracking(val id: Int) : TrackingViewAction()

    data class UpdateTracking(val tracking: Tracking, val id: Int): TrackingViewAction()
}