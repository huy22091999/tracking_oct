package com.oceantech.tracking.ui.timesheet

import com.airbnb.mvrx.*
import com.oceantech.tracking.core.TrackingBaseViewModel
import com.oceantech.tracking.data.model.TimeSheet
import com.oceantech.tracking.data.repository.TimeSheetRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.net.NetworkInterface

class TimeSheetViewModel @AssistedInject constructor(
    @Assisted state: TimeSheetViewState,
    private val repo: TimeSheetRepository
) : TrackingBaseViewModel<TimeSheetViewState, TimeSheetViewAction, TimeSheetViewEvent>(state) {

    override fun handle(action: TimeSheetViewAction) {
        when(action){
            is TimeSheetViewAction.getTimeSheetAction -> getTimeSheet()
            is TimeSheetViewAction.checkinAction -> handleCheckin(action.ip)
        }
    }

    private fun handleCheckin(ip: String) {
        setState { copy(checkin = Loading()) }
        repo.addCheckin(ip).execute {
            copy(checkin = it)
        }
    }

    private fun getTimeSheet() {
        setState { copy(timeSheets = Loading()) }
        repo.getTimeSheet().execute {
            Timber.e("${it.invoke()}" ?: " khong co data viewModel")
            copy(timeSheets = it)
        }
    }

    public fun handleReturnShowDetailTimeSheet(timeSheet : TimeSheet){
        _viewEvents.post(TimeSheetViewEvent.ReturnDetailTimeSheetViewEvent(timeSheet))
    }

    @AssistedFactory
    interface Factory{
        fun create(initialState: TimeSheetViewState) : TimeSheetViewModel
    }

    companion object : MvRxViewModelFactory<TimeSheetViewModel, TimeSheetViewState>{
        override fun create(
            viewModelContext: ViewModelContext,
            state: TimeSheetViewState
        ): TimeSheetViewModel? {
            val factory = when(viewModelContext){
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }
            return factory?.create(state) ?: error("You should let your activity/fragment implements Factory interface")
        }
    }
}