package com.oceantech.tracking.ui.profile

import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.oceantech.tracking.core.TrackingViewModel
import com.oceantech.tracking.data.repository.UserRepository
import com.oceantech.tracking.ui.home.HomeViewAction
import com.oceantech.tracking.ui.home.HomeViewEvent
import com.oceantech.tracking.ui.home.HomeViewModel
import com.oceantech.tracking.ui.home.HomeViewState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class ProfileViewModel @AssistedInject constructor(
    @Assisted state: ProfileViewState,
    val repository: UserRepository,
) : TrackingViewModel<ProfileViewState, ProfileViewAction, ProfileViewEvent>(state) {
    var language: Int = 1

    @AssistedFactory
    interface Factory {
        fun create(initialState: ProfileViewState): ProfileViewModel
    }

    companion object : MvRxViewModelFactory<ProfileViewModel, ProfileViewState> {
        @JvmStatic
        override fun create(
            viewModelContext: ViewModelContext,
            state: ProfileViewState
        ): ProfileViewModel? {
            val factory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? ProfileViewModel.Factory
                is ActivityViewModelContext -> viewModelContext.activity as? ProfileViewModel.Factory
            }
            return factory?.create(state)
                ?: error("You should let your activity/fragment implements Factory interface")
        }
    }

    override fun handle(action: ProfileViewAction) {
        when (action) {
            is ProfileViewAction.GetCurrentUser -> handleCurrentUser()
            is ProfileViewAction.ResetLang -> handResetLang()
            else -> {}
        }
    }

    private fun handResetLang() {
        _viewEvents.post(ProfileViewEvent.ResetLanguege)
    }

    private fun handleCurrentUser() {
        setState { copy(userCurrent = Loading()) }
        repository.getCurrentUser().execute {
            copy(userCurrent = it)
        }
    }
}