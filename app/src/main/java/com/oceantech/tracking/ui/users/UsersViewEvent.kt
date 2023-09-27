package com.oceantech.tracking.ui.users

import com.oceantech.tracking.core.NimpeViewEvents
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.ui.home.HomeViewEvent

sealed class UsersViewEvent : NimpeViewEvents {
    data class ReturnDetailViewEvent(val user: User) : UsersViewEvent()
    data class ReturnEditInfo(val user: User) : UsersViewEvent()
}
