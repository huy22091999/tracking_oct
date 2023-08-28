package com.oceantech.tracking.ui.users

import com.oceantech.tracking.core.NimpeViewModelAction
import com.oceantech.tracking.ui.home.HomeViewAction

sealed class UserViewAction:NimpeViewModelAction{
    object ResetLang: UserViewAction()
    object GetListUser:UserViewAction()
    object RemoveUser:UserViewAction()
    object UpdateUser:UserViewAction()
}