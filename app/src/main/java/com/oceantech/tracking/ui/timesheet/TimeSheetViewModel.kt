package com.oceantech.tracking.ui.timesheet

import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.oceantech.tracking.core.TrackingViewModel
import com.oceantech.tracking.data.repository.TimeSheetRepository
import com.oceantech.tracking.data.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import timber.log.Timber

class TimeSheetViewModel @AssistedInject constructor(
    @Assisted state: TimeSheetViewState,
    private val repo: TimeSheetRepository
) : TrackingViewModel<TimeSheetViewState, TimeSheetViewAction, TimeSheetViewEvent>(state) {
    override fun handle(action: TimeSheetViewAction) {
        when (action) {
            is TimeSheetViewAction.getTimeSheetAction -> getTimeSheet()
            is TimeSheetViewAction.checkinAction -> handleCheckin(action.ip)
        }

    }

    private fun getTimeSheet() {
        setState { copy(timeSheets = Loading()) }
        repo.getTimeSheet().execute {
            Timber.e("Hi${it.invoke()}" ?: " khong co data viewModel")
            copy(timeSheets = it)
        }
    }

    private fun handleCheckin(ip: String) {
        setState { copy(checkin = Loading()) }
        repo.addCheckin(ip).execute {
            copy(checkin = it)
        }
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