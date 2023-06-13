package com.oceantech.tracking.ui.home

import com.airbnb.mvrx.*
import com.oceantech.tracking.core.TrackingViewModel
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.data.repository.TimeSheetRepository
import com.oceantech.tracking.data.repository.TrackingRepository
import com.oceantech.tracking.data.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class HomeViewModel @AssistedInject constructor(
    @Assisted state: HomeViewState,
    val repository: UserRepository,
    val repoTracking: TrackingRepository,
    val repoCheckIn : TimeSheetRepository
) : TrackingViewModel<HomeViewState, HomeViewAction, HomeViewEvent>(state) {
    var language: Int = 1
    override fun handle(action: HomeViewAction) {
        when (action) {
            is HomeViewAction.GetCurrentUser -> handleCurrentUser()
            is HomeViewAction.ResetLang -> handResetLang()
            is HomeViewAction.GetAllUser -> handleGetAll()
            is HomeViewAction.AddTracking -> handleTracking(action.tracking)
            is HomeViewAction.CheckIn -> handleCheckIn(action.ip)
            is HomeViewAction.GetAllTimeSheet -> handleGetTimeSheet()
        }
    }

    private fun handleGetTimeSheet() {
        setState { copy(asyncTimeSheet = Loading()) }
        repoCheckIn.getTimeSheet().execute {
            copy(asyncTimeSheet = it)
        }
    }

    private fun handleCheckIn(ip: String) {
        setState { copy(asyncCheckIn = Loading()) }
        repoCheckIn.checkIn(ip).execute {
            copy(asyncCheckIn = it)
        }
    }

    private fun handleTracking(tracking: Tracking) {
        setState { copy(asyncTracking = Loading()) }
        repoTracking.tracking(tracking).execute {
            copy(asyncTracking = it)
        }
    }

    private fun handResetLang() {
        _viewEvents.post(HomeViewEvent.ResetLanguege)
    }
    private fun handleCurrentUser() {
        setState { copy(userCurrent = Loading()) }
        repository.getCurrentUser().execute {
            copy(userCurrent = it)
        }
    }
    private fun handleGetAll(){
        setState { copy(asyncListUser = Loading()) }
        repository.getAllUser().execute {
            copy(asyncListUser = it)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(initialState: HomeViewState): HomeViewModel
    }

    companion object : MvRxViewModelFactory<HomeViewModel, HomeViewState> {
        @JvmStatic
        override fun create(
            viewModelContext: ViewModelContext,
            state: HomeViewState
        ): HomeViewModel {
            val factory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }
            return factory?.create(state)
                ?: error("You should let your activity/fragment implements Factory interface")
        }
    }

}