package com.oceantech.tracking.ui.timesheet

import com.oceantech.tracking.core.NimpeViewModelAction
import java.sql.Time

sealed class TimeSheetViewAction : NimpeViewModelAction {
    object getTimeSheetAction : TimeSheetViewAction()
    data class checkinAction(val ip : String) : TimeSheetViewAction()
}