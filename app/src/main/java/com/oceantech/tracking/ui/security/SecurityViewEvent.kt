package com.oceantech.tracking.ui.security

import com.oceantech.tracking.core.NimpeViewEvents
import com.oceantech.tracking.data.model.User

sealed class SecurityViewEvent:NimpeViewEvents {
    object ReturnLoginEvent:SecurityViewEvent()

    object ReturnInforRegisterEvent:SecurityViewEvent()
    data class ReturnSigninEvent(val user : User):SecurityViewEvent()
    object ReturnResetpassEvent:SecurityViewEvent()
}