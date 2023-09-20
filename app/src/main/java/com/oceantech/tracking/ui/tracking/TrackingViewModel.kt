package com.oceantech.tracking.ui.tracking

import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.oceantech.tracking.core.TrackingViewModel
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.data.repository.TrackingRepository
import com.oceantech.tracking.ui.timesheet.TimeSheetViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import timber.log.Timber

class TrackingViewModel @AssistedInject constructor(
    @Assisted state: TrackingViewState,
    val repo: TrackingRepository
) : TrackingViewModel<TrackingViewState, TrackingViewAction, TrackingViewEvent>(state) {
    override fun handle(action: TrackingViewAction) {
        when (action) {
            is TrackingViewAction.getTrackingAction -> handleGetTracking()
            is TrackingViewAction.saveTracking -> handleSaveTracking(action.tracking)
            is TrackingViewAction.deleteTracking -> handleDeleteTracking(action.id)
        }
    }

    private fun handleDeleteTracking(id: Int) {
        setState { copy(deleteTracking = Loading()) }
        repo.deleteTracking(id).execute {
            copy(deleteTracking = it)
        }
    }

    private fun handleSaveTracking(tracking: Tracking) {
        setState { copy(Tracking = Loading()) }
        repo.saveTracking(tracking).execute {
            copy(Tracking = it)
        }
    }

    private fun handleGetTracking() {
        setState { copy(listTracking = Loading()) }
        repo.getTracking().execute {
            Timber.e("TrackingModel${it.invoke()}" ?: " khong co data viewModel")
            copy(listTracking = it)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(initialState: TrackingViewState): com.oceantech.tracking.ui.tracking.TrackingViewModel

    }

    companion object :
        MvRxViewModelFactory<com.oceantech.tracking.ui.tracking.TrackingViewModel, TrackingViewState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: TrackingViewState
        ): com.oceantech.tracking.ui.tracking.TrackingViewModel? {
            val factory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }
            return factory?.create(state)
                ?: error("You should let your activity/fragment implements Factory interface")
        }
    }

}