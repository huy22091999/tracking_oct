package com.oceantech.tracking.ui.home

import com.oceantech.tracking.core.NimpeViewModelAction
import com.oceantech.tracking.data.model.User
//done
sealed class HomeViewAction:NimpeViewModelAction{
    data class UpdateMyself(val user: User):HomeViewAction()
    object GetCurrentUser:HomeViewAction()
    object GetAllTimeSheet:HomeViewAction()
    data class CheckIn(val ip: String): HomeViewAction()
}