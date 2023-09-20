package com.oceantech.tracking.ui.tracking

import com.oceantech.tracking.core.NimpeViewModelAction
import com.oceantech.tracking.data.model.Tracking
//done
sealed class TrackingViewAction : NimpeViewModelAction{
    object GetAllTrackingByUser: TrackingViewAction()
    data class PostNewTracking(val tracking: Tracking): TrackingViewAction()
    data class UpdateTracking(val id:Int,val tracking: Tracking): TrackingViewAction()
    data class DeleteTracking(val id:Int): TrackingViewAction()
}