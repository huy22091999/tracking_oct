package com.oceantech.tracking.ui.trackings

import com.airbnb.mvrx.*
import com.oceantech.tracking.core.TrackingViewModel
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.data.repository.TrackingRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class TrackingListViewModel @AssistedInject constructor(
    @Assisted state: TrackingViewState,
    val repository: TrackingRepository
) : TrackingViewModel<TrackingViewState, TrackingViewAction, TrackingViewEvent>(state) {
    override fun handle(action: TrackingViewAction) {
        when (action) {
            is TrackingViewAction.GetAllTracking -> handleGetTracking()
            is TrackingViewAction.Delete -> handleDelete(action.tracking)
            is TrackingViewAction.Update -> handleUpdate(action.tracking,action.newTracking)
            is TrackingViewAction.AddTracking -> handleAdd(action.tracking)
        }
    }

    private fun handleAdd(tracking: Tracking) {
        setState { copy(asyncTracking = Loading()) }
        repository.tracking(tracking).execute {
            copy(asyncTracking = it)
        }
        withState { state ->
            if (state.asyncListTracking is Success) {
                state.asyncListTracking()?.add(tracking)
            }
        }
    }

    private fun handleDelete(tracking: Tracking) {
        setState { copy(asyncDelete = Loading()) }
        repository.delete(tracking).execute {
            copy(asyncDelete = it)
        }
        withState { state ->
            if (state.asyncListTracking is Success) {
                state.asyncListTracking()?.remove(tracking)
            }
        }
    }

    private fun handleGetTracking() {
        setState { copy(asyncListTracking = Loading()) }
        repository.getAllTracking().execute {
            copy(asyncListTracking = it)
        }
    }

    private fun handleUpdate(tracking: Tracking, newTracking: Tracking) {
        setState { copy(asyncUpdate = Loading()) }
        repository.updateTracking(tracking).execute {
            copy(asyncUpdate = it)
        }
        withState { state ->
            if (state.asyncListTracking is Success) {
                val list = state.asyncListTracking.invoke()
                state.asyncListTracking()?.set(list?.indexOf(tracking) ?: 0,newTracking)
            }
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