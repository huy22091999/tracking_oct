package com.oceantech.tracking.ui.tracking

import com.oceantech.tracking.core.NimpeViewModelAction

open class TrackingViewAction: NimpeViewModelAction {

    class GetAllTracking(): TrackingViewAction()
    data class SaveTracking(val firstName: String, val lastName: String ,val dob: String, val gender: String): TrackingViewAction()
}