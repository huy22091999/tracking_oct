package com.oceantech.tracking.ui.tracking

import com.oceantech.tracking.core.NimpeViewModelAction
import com.oceantech.tracking.data.model.TokenResponse
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.ui.users.UsersViewAction

sealed class TrackingViewAction : NimpeViewModelAction {
    object rcvScrollUp : TrackingViewAction()
    object rcvScrollDown : TrackingViewAction()
    object getAllTrackings : TrackingViewAction()
    data class addTrackingViewAction(val content: String) : TrackingViewAction()
    data class updateTrackingViewAction(val tracking: Tracking) : TrackingViewAction()
    data class deleteTrackingViewAction(val tracking: Tracking) : TrackingViewAction()
}