package com.oceantech.tracking.ui.security

import com.oceantech.tracking.core.NimpeViewEvents

sealed class SecurityViewEvent:NimpeViewEvents {
    object ReturnSigninEvent:SecurityViewEvent()
    object ReturnResetpassEvent:SecurityViewEvent()
}