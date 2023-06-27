package com.oceantech.tracking.ui.public_config

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.ConfigApp

data class PublicViewState(
    val config: Async<ConfigApp> = Uninitialized
):MavericksState {
}