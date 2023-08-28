package com.oceantech.tracking.ui.infomation

import com.oceantech.tracking.core.NimpeViewModelAction
import com.oceantech.tracking.data.model.User

sealed class InfoViewsAction : NimpeViewModelAction {
    object GetUserAction : InfoViewsAction()
    data class UpdateUserAction(val user: User) : InfoViewsAction()
}