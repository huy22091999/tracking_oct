package com.oceantech.tracking.ui.users

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.Page
import com.oceantech.tracking.data.model.User
//done
data class UserViewState(
    val asyncUser: Async<User> = Uninitialized,
    val asyncListUser:Async<Page<User>> = Uninitialized,
    val asyncBlockUser:Async<User> = Uninitialized,
    val asyncUpdateUser:Async<User> = Uninitialized
) : MvRxState