package com.oceantech.tracking.ui.home

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.oceantech.tracking.data.model.*

data class HomeViewState(
    val asyncCategory: Async<Page<Category>> = Uninitialized,
    val asyncNews: Async<Page<News>> = Uninitialized,
    val asyncSaveFeedback:Async<Feedback> = Uninitialized,
    val asyncListUser : Async<List<User>> = Uninitialized,
    val asyncTracking : Async<Tracking> = Uninitialized,
    val userCurrent:Async<User> = Uninitialized,
    val asyncCheckIn : Async<TimeSheet> = Uninitialized,
    val asyncTimeSheet : Async<List<TimeSheet>> = Uninitialized

) : MvRxState {
    fun isLoadding() = asyncCategory is Loading ||
            asyncNews is Loading ||
            asyncSaveFeedback is Loading ||
            userCurrent is Loading

}