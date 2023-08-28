package com.oceantech.tracking.ui.infomation

import com.oceantech.tracking.core.NimpeViewEvents
import com.oceantech.tracking.data.model.User

sealed class InfoViewsEvent : NimpeViewEvents {
    object ReturnEditEvent : InfoViewsEvent()
    data class ReturnUpdateViewEvent(val user : User) : InfoViewsEvent()
}