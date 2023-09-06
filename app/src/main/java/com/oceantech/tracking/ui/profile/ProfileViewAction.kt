package com.oceantech.tracking.ui.profile

import com.oceantech.tracking.core.NimpeViewModelAction


sealed class ProfileViewAction:NimpeViewModelAction{
    object ResetLang: ProfileViewAction()
    object GetCurrentUser:ProfileViewAction()
}
