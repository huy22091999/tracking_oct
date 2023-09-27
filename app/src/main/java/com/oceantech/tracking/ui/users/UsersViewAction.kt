package com.oceantech.tracking.ui.users

import androidx.lifecycle.LifecycleCoroutineScope
import com.oceantech.tracking.core.NimpeViewModelAction
import com.oceantech.tracking.data.model.TokenResponse
import com.oceantech.tracking.data.model.User

sealed class UsersViewAction : NimpeViewModelAction {
    object getAllUser : UsersViewAction()
    object restart : UsersViewAction()
    data class EditUser(val id: Int, val user: User) : UsersViewAction()
    data class unBlockUser(val id: Int, val user: User) : UsersViewAction()
    data class blockUser(val id: Int) : UsersViewAction()
    data class saveTokenAction(val token: TokenResponse) : UsersViewAction()
    data class RefeshUserAction(val lifecycleScope: LifecycleCoroutineScope) : UsersViewAction()
}
