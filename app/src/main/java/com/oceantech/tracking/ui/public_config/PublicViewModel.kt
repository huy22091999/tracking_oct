package com.oceantech.tracking.ui.public_config

import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.oceantech.tracking.core.TrackingViewModel
import com.oceantech.tracking.data.repository.PublicRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Singleton


class PublicViewModel @AssistedInject constructor(
    @Assisted state: PublicViewState,
    private val publicRepository: PublicRepository
): TrackingViewModel<PublicViewState, PublicViewAction, PublicViewEvent>(state) {
    override fun handle(action: PublicViewAction) {
        when(action){
            is PublicViewAction.GetConfigApp -> handleGetConfigApp()
        }
    }

    private fun handleGetConfigApp() {
        setState { copy(config = Loading()) }
        publicRepository.getConfigApp().execute {
            copy(config = it)
        }
    }

    @AssistedFactory
    interface Factory{
        fun create(state: PublicViewState): PublicViewModel
    }

    companion object: MvRxViewModelFactory<PublicViewModel, PublicViewState>{
        override fun create(
            viewModelContext: ViewModelContext,
            state: PublicViewState
        ): PublicViewModel? {
            val factory = when(viewModelContext){
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
            }
            return factory?.create(state) ?: error("You must implement your activity/fragment to PublicViewModel.Factory")
        }
    }
}