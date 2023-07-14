package com.oceantech.tracking.ui.home

import com.oceantech.tracking.core.NimpeViewModelAction
import com.oceantech.tracking.data.model.Feedback
import com.oceantech.tracking.data.model.User

sealed class HomeViewAction:NimpeViewModelAction{

    object GetCurrentUser:HomeViewAction()
    data class ResetLang(val lang: String):HomeViewAction()

    object GetAllUsers: HomeViewAction()

    data class GetDevice(val tokenDevice: String): HomeViewAction()

    data class GetBlockUser(val id: Int): HomeViewAction()

    data class UpdateMyself(val user: User): HomeViewAction()

    data class UpdateUser(val user: User, val id: Int): HomeViewAction()

}