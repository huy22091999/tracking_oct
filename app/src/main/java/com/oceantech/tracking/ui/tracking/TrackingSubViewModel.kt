package com.oceantech.tracking.ui.tracking

import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.oceantech.tracking.core.TrackingViewModel
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.data.repository.TrackingRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
//done
class TrackingSubViewModel @AssistedInject constructor(
    @Assisted val state: TrackingViewState,
    private val trackingRepository: TrackingRepository,
) : TrackingViewModel<TrackingViewState, TrackingViewAction, TrackingViewEvent>(state) {

    override fun handle(action: TrackingViewAction) {
        when (action) {
            is TrackingViewAction.GetAllTrackingByUser -> handleGetAllTracking()
            is TrackingViewAction.PostNewTracking -> handlePostNewTracking(action.tracking)
            is TrackingViewAction.UpdateTracking -> handleUpdateTracking(action.id, action.tracking)
            is TrackingViewAction.DeleteTracking -> handleDeleteTracking(action.id)
        }
    }

    private fun handleDeleteTracking(id: Int) {
        setState {
            this.copy(asyncDeleteTracking = Loading())
        }
        trackingRepository.deleteTracking(id).execute {
            copy(asyncDeleteTracking = it)
        }
    }

    private fun handleUpdateTracking(id: Int, tracking: Tracking) {
        setState {
            this.copy(asyncUpdateTracking = Loading())
        }
        trackingRepository.updateTracking(id, tracking).execute {
            copy(asyncUpdateTracking = it)
        }
    }

    private fun handlePostNewTracking(tracking: Tracking) {
        setState {
            this.copy(asyncSaveTracking = Loading())
        }
        trackingRepository.postNewTracking(tracking).execute {
            copy(asyncSaveTracking = it)
        }
    }

    private fun handleGetAllTracking() {
        setState {
            copy(asyncTrackingArray = Loading())
        }
        trackingRepository.getAllTrackingByUser().execute {
            copy(asyncTrackingArray = it)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(initialState: TrackingViewState): TrackingSubViewModel
    }

    companion object : MvRxViewModelFactory<TrackingSubViewModel, TrackingViewState> {
        @JvmStatic
        override fun create(
            viewModelContext: ViewModelContext,
            state: TrackingViewState
        ): TrackingSubViewModel {
            val factory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }
            return factory?.create(state)
                ?: error("You should let your activity/fragment implements Factory interface")
        }
    }
}