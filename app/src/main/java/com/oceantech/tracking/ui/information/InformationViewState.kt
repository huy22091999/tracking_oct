package com.oceantech.tracking.ui.information

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.User

// Các class state chứa tất cả dữ liệu bạn cần để hiển thị màn hình
data class InformationViewState(
    val user: Async<User> = Uninitialized
) : MvRxState {
    fun isLoadding() =
        user is Loading
}
