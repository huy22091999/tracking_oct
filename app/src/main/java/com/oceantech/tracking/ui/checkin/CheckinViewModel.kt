package com.oceantech.tracking.ui.checkin

import com.airbnb.mvrx.*
import com.oceantech.tracking.core.TrackingViewModel
import com.oceantech.tracking.data.repository.TimeSheetRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class CheckinViewModel @AssistedInject constructor(
    @Assisted state: CheckinViewState,
    val repository: TimeSheetRepository,
)
    : TrackingViewModel<CheckinViewState,CheckinViewAction,CheckinViewEvent>(state) {
    override fun handle(action: CheckinViewAction) {
        when(action){
            is CheckinViewAction.Checkin -> handleCheckin(action.ip)
            is CheckinViewAction.GetTimeSheet -> handleGetTimeSheet()
            is CheckinViewAction.GetIp -> handleGetIp()
        }
    }

    private fun handleGetIp() {
        setState { copy(asyncIp = Loading()) }
        repository.getIp().execute {
            copy(asyncIp = it)
        }
    }


    private fun handleGetTimeSheet() {
        setState { copy(asyncTimeSheet = Loading()) }
        repository.getTimeSheet().execute {
            copy(asyncTimeSheet = it)
        }
    }

    private fun handleCheckin(ip: String) {
        setState { copy(asyncCheckin = Loading()) }
        repository.checkIn(ip).execute {
            if (it is Success){
                repository.getTimeSheet().execute {checkinState->
                    copy(asyncTimeSheet = checkinState)
                }
            }
            copy(asyncCheckin = it)
        }
    }
    @AssistedFactory
    interface Factory {
        fun create(initialState : CheckinViewState) : CheckinViewModel
    }

    companion object : MvRxViewModelFactory<CheckinViewModel,CheckinViewState>{
        @JvmStatic
        override fun create(
            viewModelContext: ViewModelContext,
            state: CheckinViewState
        ): CheckinViewModel {
            val factory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }
            return factory?.create(state) ?: error("You should let your activity/fragment implements Factory interface")
        }
    }
}