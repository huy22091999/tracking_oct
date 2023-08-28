package com.oceantech.tracking.ui.users

import com.oceantech.tracking.core.NimpeViewEvents
import com.oceantech.tracking.data.model.User

// liên quan đến hành động người dùng
sealed class UsersViewEvent : NimpeViewEvents {
    data class ReturnDetailViewEvent(val user : User) : UsersViewEvent()
}