package com.oceantech.tracking.ui.timesheets

import com.oceantech.tracking.core.NimpeViewModelAction

open class TimeSheetViewAction: NimpeViewModelAction {

    data class CheckInAction(val ip: String): TimeSheetViewAction()

    object AllTimeSheets: TimeSheetViewAction()
}