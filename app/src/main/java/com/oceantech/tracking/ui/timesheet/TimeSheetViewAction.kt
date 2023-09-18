package com.oceantech.tracking.ui.timesheet

import com.oceantech.tracking.core.NimpeViewModelAction

sealed class TimeSheetViewAction : NimpeViewModelAction {
    object getTimeSheetAction : TimeSheetViewAction()
    data class checkinAction(val ip: String) : TimeSheetViewAction()
}
