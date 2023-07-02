package com.oceantech.tracking.ui.timesheets

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.TimeSheet

data class TimeSheetViewState(
    val checkInState: Async<TimeSheet> = Uninitialized,
    val getAllTimeSheetState: Async<List<TimeSheet>> = Uninitialized
):MavericksState {

    fun isLoading() = checkInState is Loading || getAllTimeSheetState is Loading
}
