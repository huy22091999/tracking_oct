package com.oceantech.tracking.ui.timesheet

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.TimeSheet

data class TimeSheetViewState(
    val timeSheets: Async<List<TimeSheet>> = Uninitialized,
    val checkin: Async<TimeSheet> = Uninitialized
) : MvRxState {
}