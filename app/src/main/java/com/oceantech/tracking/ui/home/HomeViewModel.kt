package com.oceantech.tracking.ui.home

import com.airbnb.mvrx.*
import com.oceantech.tracking.core.TrackingViewModel
import com.oceantech.tracking.data.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class HomeViewModel @AssistedInject constructor(
    @Assisted state: HomeViewState,
    val repository: UserRepository,
) : TrackingViewModel<HomeViewState, HomeViewAction, HomeViewEvent>(state) {
    var language: Int = 1
    override fun handle(action: HomeViewAction) {
        when (action) {
            is HomeViewAction.GetCurrentUser -> handleCurrentUser()
            is HomeViewAction.ResetLang -> handResetLang(action.lang)
            is HomeViewAction.GetAllUsers -> handleAllUsers()
        }
    }

    private fun handleAllUsers() {
        setState {
            copy(allUsers = Loading())
        }
        repository.getAllUsers().execute {
            copy(allUsers = it)
        }
    }

    private fun handResetLang(lang: String) {
        _viewEvents.post(HomeViewEvent.ResetLanguage(lang))
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