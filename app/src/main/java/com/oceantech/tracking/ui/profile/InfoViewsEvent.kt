package com.oceantech.tracking.ui.profile

import com.oceantech.tracking.core.NimpeViewEvents
import com.oceantech.tracking.data.model.User

sealed class InfoViewsEvent : NimpeViewEvents {
    data class ReturnNavigateToFrgViewEvent(val id: Int) : InfoViewsEvent()
    object ReturnBacktoFrgViewEvent : InfoViewsEvent()
}