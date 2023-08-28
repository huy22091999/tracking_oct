package com.oceantech.tracking.ui.tracking

import com.oceantech.tracking.core.NimpeViewModelAction
import com.oceantech.tracking.data.model.Tracking

sealed class TrackingViewAction : NimpeViewModelAction{
    object GetCurrentUser: TrackingViewAction()
    object GetAllTrackingByUser: TrackingViewAction()
    object ResetLang: TrackingViewAction()
    object NavigateToAddDialog : TrackingViewAction()
    data class PostNewTracking(val tracking: Tracking): TrackingViewAction()
    data class UpdateTracking(val id:Int,val tracking: Tracking): TrackingViewAction()
    data class DeleteTracking(val id:Int): TrackingViewAction()
}