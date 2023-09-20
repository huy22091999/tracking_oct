package com.oceantech.tracking.ui.security

import com.oceantech.tracking.core.NimpeViewEvents

sealed class SecurityViewEvent:NimpeViewEvents {
    data class ReturnShowToolBar(val isVisible: Boolean):SecurityViewEvent()
    object ReturnSigninEvent:SecurityViewEvent()
    object ReturnResetpassEvent:SecurityViewEvent()
}