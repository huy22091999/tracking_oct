package com.oceantech.tracking.ui.timesheet

import com.oceantech.tracking.core.NimpeViewEvents
import com.oceantech.tracking.data.model.TimeSheet

sealed class TimeSheetViewEvent : NimpeViewEvents {
    data class ReturnDetailTimeSheetViewEvent(val timeSheet : TimeSheet) : TimeSheetViewEvent()
}