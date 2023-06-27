package com.oceantech.tracking.ui.timesheets

import androidx.lifecycle.ViewModelProvider
import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.oceantech.tracking.core.TrackingViewModel
import com.oceantech.tracking.data.repository.TimeSheetRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject
import javax.inject.Singleton


class TimeSheetViewModel @AssistedInject constructor(
    @Assisted state: TimeSheetViewState,
    private val timeSheetRepo: TimeSheetRepository
): TrackingViewModel<TimeSheetViewState, TimeSheetViewAction, TimeSheetViewEvent>(state) {
    override fun handle(action: TimeSheetViewAction) {
        when(action){
            is TimeSheetViewAction.CheckInAction -> handleCheckIn(action.ip)
            is TimeSheetViewAction.AllTimeSheets -> handleAllTimeSheets()
        }
    }

    private fun handleAllTimeSheets() {
        setState { copy(getAllTimeSheetState = Loading()) }
        timeSheetRepo.getAllTimeSheets().execute {
            copy(getAllTimeSheetState = it)
        }
    }

    private fun handleCheckIn(ip: String) {
        setState { copy(checkInState = Loading()) }
        timeSheetRepo.checkIn(ip).execute {
            copy(checkInState = it)
        }
    }

    @AssistedFactory
    interface Factory{
        fun create(S: TimeSheetViewState): TimeSheetViewModel
    }

    companion object : MavericksViewModelFactory<TimeSheetViewModel, TimeSheetViewState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: TimeSheetViewState
        ): TimeSheetViewModel? {
            val factory = when(viewModelContext){
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }

            return factory?.create(state) ?: error("You must extends your activity/ fragment from TimeSheetViewModel.Factory ")
        }
    }
}