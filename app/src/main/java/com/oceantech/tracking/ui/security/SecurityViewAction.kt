package com.oceantech.tracking.ui.security

import com.oceantech.tracking.core.NimpeViewModelAction
import com.oceantech.tracking.data.model.TokenResponse

sealed class SecurityViewAction : NimpeViewModelAction {
    data class LogginAction(var userName: String, var password: String) : SecurityViewAction()
    data class SaveTokenAction(var token: TokenResponse) : SecurityViewAction()
    data class SignAction(var username:String, var displayName:String, var email:String, var firstName:String, var lastName:String, var password:String):SecurityViewAction()
    object GetUserCurrent : SecurityViewAction()
    object GetAllUser : SecurityViewAction()
}