package com.oceantech.tracking.ui.notification

import com.oceantech.tracking.core.NimpeViewModelAction
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.ui.information.InformationViewActon

sealed class NotificationViewAction : NimpeViewModelAction {
    object getAllNotification : NotificationViewAction()
}
