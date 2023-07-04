package com.oceantech.tracking.ui.security

import com.oceantech.tracking.core.NimpeViewEvents
import com.oceantech.tracking.data.model.User

sealed class SecurityViewEvent:NimpeViewEvents {
    object ReturnSigninEvent:SecurityViewEvent()
    object ReturnResetpassEvent:SecurityViewEvent()
    object ReturnLoginEvent:SecurityViewEvent()
    data class ReturnNextSignInEvent(val user:User):SecurityViewEvent()
}