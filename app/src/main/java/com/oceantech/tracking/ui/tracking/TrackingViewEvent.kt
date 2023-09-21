package com.oceantech.tracking.ui.tracking

import com.oceantech.tracking.core.NimpeViewEvents
import com.oceantech.tracking.data.model.Tracking

sealed class TrackingViewEvent : NimpeViewEvents {
    object ReturnDeleteTracking : TrackingViewEvent()
    data class ReturnSaveTracking(val tracking: Tracking) : TrackingViewEvent()
    data class ReturnUpdateTracking(val tracking: String?, val positionToSelected: Int) : TrackingViewEvent()
    data class ReturnGetTracking(val listTracking: List<Tracking>) : TrackingViewEvent()
}
