package com.oceantech.tracking.ui.notifications

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.Notification

data class NotificationViewState(
    val getNotification: Async<List<Notification>> = Uninitialized
): MavericksState {

    fun isLoading() = getNotification is Loading
}