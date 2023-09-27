package com.oceantech.tracking.ui.users

import androidx.paging.PagingData
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.User

data class UserViewState(
    val users: Async<User> = Uninitialized,
    val pageUsers: Async<PagingData<User>> = Uninitialized,
    var userCurrent: Async<User> = Uninitialized,
    var userEdit: Async<User> = Uninitialized,
    val blockUser: Async<User> = Uninitialized
) : MvRxState {
    fun isLoadding() = pageUsers is Loading || userCurrent is Loading
}
