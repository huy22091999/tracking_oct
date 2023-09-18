package com.oceantech.tracking.ui.timesheet

import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.oceantech.tracking.core.TrackingViewModel
import com.oceantech.tracking.data.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class TimeSheetViewModel @AssistedInject constructor(
    @Assisted state: TimeSheetViewState,
    private val repo: UserRepository
) : TrackingViewModel<TimeSheetViewState, TimeSheetViewAction, TimeSheetViewEvent>(state) {
    override fun handle(action: TimeSheetViewAction) {

    }

    @AssistedFactory
    interface Factory {
        fun create(initialState: TimeSheetViewState): TimeSheetViewModel
    }

    companion object : MvRxViewModelFactory<TimeSheetViewModel, TimeSheetViewState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: TimeSheetViewState
        ): TimeSheetViewModel? {
            val factory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }
            return factory?.create(state)
                ?: error("You should let your activity/fragment implements Factory interface")
        }
    }
}