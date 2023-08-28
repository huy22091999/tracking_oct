package com.oceantech.tracking.ui.tracking

import android.view.View
import com.oceantech.tracking.core.NimpeViewEvents
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.ui.users.UsersViewEvent

sealed class TrackingViewEvent : NimpeViewEvents {
    data class ReturnShowDialogViewEvent(val tracking: Tracking?) : TrackingViewEvent()
    data class ReturnShowOptionMenuViewEvent(val view: View, val tracking: Tracking) : TrackingViewEvent()
}