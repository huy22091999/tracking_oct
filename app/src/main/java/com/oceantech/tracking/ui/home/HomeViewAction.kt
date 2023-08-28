package com.oceantech.tracking.ui.home

import com.oceantech.tracking.core.NimpeViewModelAction
import com.oceantech.tracking.data.model.Feedback
import com.oceantech.tracking.data.model.User

sealed class HomeViewAction:NimpeViewModelAction{

    data class UpdateMyself(val user: User):HomeViewAction()

    object GetCurrentUser:HomeViewAction()
    object GetCategorys:HomeViewAction()
    object ResetLang:HomeViewAction()

    object GetAllTimeSheet:HomeViewAction()
    data class CheckIn(val ip: String): HomeViewAction()


}