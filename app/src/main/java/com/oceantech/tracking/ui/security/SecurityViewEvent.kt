package com.oceantech.tracking.ui.security

import com.oceantech.tracking.core.NimpeViewEvents
import com.oceantech.tracking.data.model.User
//done
sealed class SecurityViewEvent:NimpeViewEvents {
    object ReturnLoginEvent:SecurityViewEvent()
    object ReturnInfoRegisterEvent:SecurityViewEvent()
    data class ReturnSignUpEvent(val user : User):SecurityViewEvent()
    object ReturnResetPassEvent:SecurityViewEvent()
}