package com.oceantech.tracking.ui.security

import com.oceantech.tracking.core.NimpeViewModelAction
import com.oceantech.tracking.data.model.TokenResponse
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.ui.home.HomeViewAction

sealed class SecurityViewAction : NimpeViewModelAction {
    data class LogginAction(var userName: String, var password: String) : SecurityViewAction()
    data class SaveTokenAction(var token: TokenResponse) : SecurityViewAction()
    data class SignAction(var user:User): SecurityViewAction()
    object GetUserCurrent : SecurityViewAction()
    object GetConfigApp: SecurityViewAction()
    object EditTokenDevice: SecurityViewAction()
}