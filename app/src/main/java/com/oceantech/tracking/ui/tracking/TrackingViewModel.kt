package com.oceantech.tracking.ui.tracking

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.ViewModelContext
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingViewModel
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.data.repository.TrackingRepository
import com.oceantech.tracking.ui.security.SecurityViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject
import javax.inject.Singleton
@SuppressLint("LogNotTimber")
class TrackingViewModel @AssistedInject constructor(
    @Assisted trackingState: TrackingViewState,
    private val repository: TrackingRepository
): TrackingViewModel<TrackingViewState, TrackingViewAction, TrackingViewEvent>(trackingState) {


    override fun handle(action: TrackingViewAction) {
        when(action){
            is TrackingViewAction.SaveTracking -> handleSaveTracking(action.content)
            is TrackingViewAction.GetAllTracking -> handleGetAllTracking()
            is TrackingViewAction.DeleteTracking -> handleDeleteTracking(action.id)
            is TrackingViewAction.UpdateTracking -> handleUpdateTracking(action.tracking, action.id)
        }
    }


    private fun handleUpdateTracking(tracking: Tracking, id: Int) {
        setState { copy(updateTracking = Loading()) }
        repository.updateTracking(tracking, id).execute {
            copy(updateTracking = it)
        }

    }

    private fun handleDeleteTracking(id: Int) {
        setState { copy(deleteTracking = Loading()) }
        repository.deleteTracking(id).execute {
            copy(deleteTracking = it)
        }

    }

    private fun handleSaveTracking(content: String){
        setState { copy(saveTracking = Loading()) }
        repository.saveTracking(content).execute {
            copy(saveTracking = it)
        }

    }

    private fun handleGetAllTracking(){
        setState { copy(getAllTracking = Loading())}
        repository.getAllTracking().execute {
            copy(getAllTracking = it)
        }
    }

    @AssistedFactory
    interface Factory{
        fun create(state: TrackingViewState): com.oceantech.tracking.ui.tracking.TrackingViewModel
    }


    companion object :
        MavericksViewModelFactory<com.oceantech.tracking.ui.tracking.TrackingViewModel, TrackingViewState> {
        @JvmStatic
        override fun create(
            viewModelContext: ViewModelContext,
            state: TrackingViewState
        ): com.oceantech.tracking.ui.tracking.TrackingViewModel? {
            val factory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }

            return factory?.create(state)
                ?: error("You should let your activity/fragment implements TrackingViewModel.Factory interface")
        }
    }
}