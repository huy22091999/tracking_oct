package com.oceantech.tracking.ui.information

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.ConfigApp

data class InfoViewState(
    val config: Async<ConfigApp> = Uninitialized
):MavericksState {
}