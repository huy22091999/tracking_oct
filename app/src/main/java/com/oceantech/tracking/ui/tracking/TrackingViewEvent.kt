package com.oceantech.tracking.ui.tracking

import com.oceantech.tracking.core.NimpeViewEvents
import com.oceantech.tracking.ui.home.HomeViewEvent

sealed class TrackingViewEvent: NimpeViewEvents {
    object ResetLanguege: TrackingViewEvent()
    object NavigateToAddDialog : TrackingViewEvent()
}