package com.oceantech.tracking.ui.tracking

import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.oceantech.tracking.core.TrackingViewModel
import com.oceantech.tracking.data.repository.TrackingRepository
import com.oceantech.tracking.ui.security.SecurityViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject
import javax.inject.Singleton

class TrackingViewModel @AssistedInject constructor(
    @Assisted trackingState: TrackingViewState,
    private val repository: TrackingRepository
): TrackingViewModel<TrackingViewState, TrackingViewAction, TrackingViewEvent>(trackingState) {


    override fun handle(action: TrackingViewAction) {
        when(action){
            is TrackingViewAction.SaveTracking -> handleSaveTracking(action.firstName, action.lastName, action.dob, action.gender)
            is TrackingViewAction.GetAllTracking -> handleGetAllTracking()
        }
    }

    private fun handleSaveTracking(firstName: String, lastName: String, dob: String, gender: String){
        setState { copy(saveTracking = Loading()) }
        repository.saveTracking(firstName, lastName, dob, gender).execute {
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


    companion object : MvRxViewModelFactory<com.oceantech.tracking.ui.tracking.TrackingViewModel, TrackingViewState>{
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