package com.oceantech.tracking.ui.trackings

import com.airbnb.mvrx.*
import com.oceantech.tracking.core.TrackingViewModel
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.data.repository.TrackingRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class TrackingListViewModel @AssistedInject constructor(
    @Assisted state: TrackingViewState,
    val repository: TrackingRepository
) : TrackingViewModel<TrackingViewState, TrackingViewAction, TrackingViewEvent>(state) {
    override fun handle(action: TrackingViewAction) {
        when (action) {
            is TrackingViewAction.GetAllTracking -> handleGetTracking()
            is TrackingViewAction.Delete -> handleDelete(action.tracking)
            is TrackingViewAction.Update -> handleUpdate(action.newTracking)
            is TrackingViewAction.AddTracking -> handleAdd(action.tracking)
        }
    }

    private fun handleAdd(tracking: Tracking) {
        setState { copy(asyncTracking = Loading()) }
        repository.tracking(tracking).execute {
            if (it is Success) {
                repository.getAllTracking().execute {listState->
                    copy(asyncListTracking = listState)
                }
            }
            copy(asyncTracking = it)
        }
    }

    private fun handleDelete(tracking: Tracking) {
        setState { copy(asyncDelete = Loading()) }
        repository.delete(tracking).execute {
            if (it is Success) {
                repository.getAllTracking().execute {listState->
                    copy(asyncListTracking = listState)
                }
            }
            copy(asyncDelete = it)
        }
    }

    private fun handleGetTracking() {
        setState { copy(asyncListTracking = Loading()) }
        repository.getAllTracking().execute {
            copy(asyncListTracking = it)
        }
    }

    private fun handleUpdate(newTracking: Tracking) {
        setState { copy(asyncUpdate = Loading()) }
        repository.updateTracking(newTracking).execute {
            if (it is Success) {
                repository.getAllTracking().execute {listState->
                    copy(asyncListTracking = listState)
                }
            }
            copy(asyncUpdate = it)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(initialState: TrackingViewState): TrackingListViewModel
    }

    companion object : MvRxViewModelFactory<TrackingListViewModel, TrackingViewState> {
        @JvmStatic
        override fun create(
            viewModelContext: ViewModelContext,
            state: TrackingViewState
        ): TrackingListViewModel {
            val factory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }
            return factory?.create(state)
                ?: error("You should let your activity/fragment implements Factory interface")
        }
    }
}