package com.oceantech.tracking.ui.profile

import com.oceantech.tracking.core.NimpeViewModelAction


sealed class ProfileViewAction : NimpeViewModelAction {
    data class ResetLang(var lang: String) : ProfileViewAction()
    object GetCurrentUser : ProfileViewAction()
}
