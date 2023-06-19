package com.oceantech.tracking.ui.timesheets

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.TimeSheet

data class TimeSheetViewState(
    var checkInState: Async<TimeSheet> = Uninitialized,
    var getAllTimeSheetState: Async<List<TimeSheet>> = Uninitialized
):MvRxState {
}