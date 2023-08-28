package com.oceantech.tracking.ui.infomation

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.User

data class InfoViewsState(
    val user : Async<User>  = Uninitialized
) : MvRxState {
    fun isLoadding() = user is Loading
}