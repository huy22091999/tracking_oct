package com.oceantech.tracking.ui.profile

import com.oceantech.tracking.core.NimpeViewModelAction
import com.oceantech.tracking.data.model.User

sealed class InfoViewsAction : NimpeViewModelAction {
    object GetMyUserAction : InfoViewsAction()
    object GetUserCurentAction : InfoViewsAction()
    data class GetUserCurentByID(val userId: String?, val isMyProfile: Boolean) : InfoViewsAction()
    data class UpdateUserAction(val user: User) : InfoViewsAction()
    data class CheckIsAdmin(val user: User) : InfoViewsAction()
    object BlockUser : InfoViewsAction()
    object UnblockUser : InfoViewsAction()
    object RemoveUpdateUserAction : InfoViewsAction()

    data class VerifyUserAction(val userName: String?, val pasword: String?) : InfoViewsAction()
}