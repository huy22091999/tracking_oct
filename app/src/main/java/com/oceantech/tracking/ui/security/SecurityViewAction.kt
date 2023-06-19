package com.oceantech.tracking.ui.security

import com.oceantech.tracking.core.NimpeViewModelAction
import com.oceantech.tracking.data.model.TokenResponse

sealed class SecurityViewAction : NimpeViewModelAction {
    data class LogginAction(var userName: String, var password: String) : SecurityViewAction()
    data class SaveTokenAction(var token: TokenResponse) : SecurityViewAction()
    object GetUserCurrent : SecurityViewAction()
    data class SignInAction(
        var userName: String,
        var password: String,
        var displayName: String
    ):SecurityViewAction()

    object GetAllUsers: SecurityViewAction()
}