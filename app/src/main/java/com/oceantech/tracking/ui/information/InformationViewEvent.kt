package com.oceantech.tracking.ui.information

import com.oceantech.tracking.core.NimpeViewEvents
import com.oceantech.tracking.data.model.User

sealed class InformationViewEvent:NimpeViewEvents{
    object ReturnEditEvent : InformationViewEvent()
    data class ReturnUpdateViewEvent(val user : User) : InformationViewEvent()
}
