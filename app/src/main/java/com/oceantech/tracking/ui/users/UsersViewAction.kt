package com.oceantech.tracking.ui.users

import androidx.lifecycle.LifecycleCoroutineScope
import com.oceantech.tracking.core.NimpeViewModelAction
import com.oceantech.tracking.data.model.TokenResponse
import com.oceantech.tracking.ui.security.SecurityViewAction

// liên quan đến dữ liệu
sealed class UsersViewAction : NimpeViewModelAction {
    object GetAllUsers : UsersViewAction()
    data class SaveTokenAction(val token: TokenResponse) : UsersViewAction()
    data class RefeshUserAction(val lifecycleScope: LifecycleCoroutineScope) : UsersViewAction()
}