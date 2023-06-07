package com.oceantech.tracking.ui.home

import android.util.Log
import com.airbnb.mvrx.*
import com.oceantech.tracking.core.TrackingViewModel
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.repository.TrackingRepository
import com.oceantech.tracking.data.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class HomeViewModel @AssistedInject constructor(
    @Assisted state: HomeViewState,
    val repository: UserRepository,
    val trackingRepo: TrackingRepository
) : TrackingViewModel<HomeViewState, HomeViewAction, HomeViewEvent>(state) {
    var language: Int = 1
    init {
        handleAllTracking()
    }
    override fun handle(action: HomeViewAction) {
        when (action) {
            is HomeViewAction.GetCurrentUser -> handleCurrentUser()
            is HomeViewAction.ResetLang -> handResetLang()
            is HomeViewAction.GetTrackings -> handleAllTracking()
            is HomeViewAction.SaveTracking -> handleSaveTracking(action.content)
            is HomeViewAction.UpdateTracking -> handleUpdateTracking(action.id, action.content)
            is HomeViewAction.DeleteTracking -> handleDeleteTracking(action.id)
        }
    }

    private fun handleDeleteTracking(id: Int) {
        setState { copy(asyncDeleteTracking = Loading()) }
        trackingRepo.delete(id).execute {
            copy(asyncDeleteTracking = it)
        }
    }

    private fun handleUpdateTracking(id:Int, content:String) {
        setState { copy(asyncUpdateTracking = Loading()) }
        trackingRepo.update(id, content).execute {
            copy(asyncUpdateTracking = it)
        }
    }

    private fun handleAllTracking() {
        setState { copy(allTracking = Loading()) }
        trackingRepo.getAllByUser().execute {
            copy(allTracking = it)
        }
    }

    private fun handleSaveTracking(content:String) {
        setState { copy(asyncSaveTracking = Loading()) }
        trackingRepo.save(content).execute {
            copy(asyncSaveTracking = it)
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