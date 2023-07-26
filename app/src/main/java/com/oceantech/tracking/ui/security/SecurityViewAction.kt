package com.oceantech.tracking.ui.security

import com.oceantech.tracking.core.NimpeViewModelAction
import com.oceantech.tracking.data.model.TokenResponse

sealed class SecurityViewAction : NimpeViewModelAction {
    data class LogginAction(var userName: String, var password: String) : SecurityViewAction()
    data class SaveTokenAction(var token: TokenResponse) : SecurityViewAction()
    object GetUserCurrent : SecurityViewAction()
    data class SignInAction(
        val userName: String,
        val password: String,
        val displayName: String,
        val gender: String,
        val dob: String,
        val email: String,
        val university: String,
        val year: Int,
        val confirmPassword: String
    ):SecurityViewAction()


}