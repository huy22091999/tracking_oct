package com.oceantech.tracking.ui.information

import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.oceantech.tracking.core.TrackingViewModel
import com.oceantech.tracking.data.repository.PublicRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject


class InfoViewModel @AssistedInject constructor(
    @Assisted state: InfoViewState,
    private val publicRepository: PublicRepository
): TrackingViewModel<InfoViewState, InfoViewAction, InfoViewEvent>(state) {
    override fun handle(action: InfoViewAction) {
        when(action){
            is InfoViewAction.GetConfigApp -> handleGetConfigApp()
        }
    }

    private fun handleGetConfigApp() {
        setState { copy(config = Loading()) }
        publicRepository.getConfigApp().execute{
            copy(config = it)
        }
    }

    @AssistedFactory
    interface Factory{
        fun create(state: InfoViewState): InfoViewModel
    }


    companion object : MavericksViewModelFactory<InfoViewModel, InfoViewState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: InfoViewState
        ): InfoViewModel? {
            val factory = when(viewModelContext){
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
            }
            return factory?.create(state) ?: error("You must implement your activity/fragment to PublicViewModel.Factory")
        }
    }
}