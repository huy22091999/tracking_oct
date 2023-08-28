package com.oceantech.tracking.ui.users

import com.oceantech.tracking.core.NimpeViewEvents
import com.oceantech.tracking.ui.home.HomeViewEvent

sealed class UserViewEvent: NimpeViewEvents {
    object ResetLanguege: UserViewEvent()
}