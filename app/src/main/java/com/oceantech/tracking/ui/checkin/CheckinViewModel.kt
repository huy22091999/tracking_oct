package com.oceantech.tracking.ui.checkin

import com.airbnb.mvrx.*
import com.oceantech.tracking.core.TrackingViewModel
import com.oceantech.tracking.data.repository.TimeSheetRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class CheckinViewModel @AssistedInject constructor(
    @Assisted state: CheckinViewState,
    val repository: TimeSheetRepository
)
    : TrackingViewModel<CheckinViewState,CheckinViewAction,CheckinViewEvent>(state) {
    override fun handle(action: CheckinViewAction) {
        when(action){
            is CheckinViewAction.Checkin -> handleCheckin(action.ip)
            is CheckinViewAction.GetTimeSheet -> handleGetTimeSheet()
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
            val newSate = copy(asyncCheckin = it)
            if (it is Success){
                newSate.asyncTimeSheet = Loading()
                repository.getTimeSheet().execute {checkinState->
                    copy(asyncTimeSheet = checkinState)
                }
            }
            newSate
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