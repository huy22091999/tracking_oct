package com.oceantech.tracking.ui.notification

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.Notification

data class NotificationViewState(val mListnotification: Async<List<Notification>> = Uninitialized)
