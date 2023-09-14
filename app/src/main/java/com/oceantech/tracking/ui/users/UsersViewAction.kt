package com.oceantech.tracking.ui.users

import androidx.lifecycle.LifecycleCoroutineScope
import com.oceantech.tracking.core.NimpeViewModelAction
import com.oceantech.tracking.data.model.TokenResponse

sealed class UsersViewAction : NimpeViewModelAction {
    object getAllUser : UsersViewAction()
    data class saveTokenAction(val token: TokenResponse) : UsersViewAction()
    data class RefeshUserAction(val lifecycleScope: LifecycleCoroutineScope) : UsersViewAction()
}
