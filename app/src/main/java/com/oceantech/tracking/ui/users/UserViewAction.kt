package com.oceantech.tracking.ui.users

import com.oceantech.tracking.core.NimpeViewModelAction
import com.oceantech.tracking.data.model.User
//done
sealed class UserViewAction:NimpeViewModelAction{
    data class GetUserById(val id: Int): UserViewAction()
    object GetListUser:UserViewAction()
    data class UpdateUser(val user: User):UserViewAction()
    data class BlockUserById(val id:Int): UserViewAction()
    data class UnBlockUserById(val user: User): UserViewAction()
}