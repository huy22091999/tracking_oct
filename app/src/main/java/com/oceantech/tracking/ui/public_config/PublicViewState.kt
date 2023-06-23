package com.oceantech.tracking.ui.public_config

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.ConfigApp

data class PublicViewState(
    var config: Async<ConfigApp> = Uninitialized
):MvRxState {
}