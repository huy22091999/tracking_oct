package com.oceantech.tracking.ui.security

import com.oceantech.tracking.core.NimpeViewModelAction
import com.oceantech.tracking.data.model.TokenResponse
import com.oceantech.tracking.data.model.User

sealed class SecurityViewAction : NimpeViewModelAction {
    data class LogginAction(var userName: String, var password: String) : SecurityViewAction()
    data class SignAction(val user: User) : SecurityViewAction()
    data class SaveTokenAction(var token: TokenResponse) : SecurityViewAction()
    object GetUserCurrent : SecurityViewAction()
}