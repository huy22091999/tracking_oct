package com.oceantech.tracking.ui.security

import com.oceantech.tracking.core.NimpeViewModelAction
import com.oceantech.tracking.data.model.TokenResponse
import com.oceantech.tracking.data.model.User
//done
sealed class SecurityViewAction : NimpeViewModelAction {
    data class LoginAction(var userName: String, var password: String) : SecurityViewAction()
    data class SaveTokenAction(var token: TokenResponse) : SecurityViewAction()
    class SignupAction(var user: User) : SecurityViewAction()
    object GetUserCurrent : SecurityViewAction()

}