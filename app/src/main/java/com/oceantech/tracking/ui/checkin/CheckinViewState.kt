package com.oceantech.tracking.ui.checkin

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.TimeSheet

data class CheckinViewState(
    var asyncCheckin : Async<TimeSheet> = Uninitialized,
    var asyncTimeSheet : Async<List<TimeSheet>> = Uninitialized,
    var asyncIp : Async<String> = Uninitialized
) : MvRxState