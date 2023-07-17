package com.oceantech.tracking.ui.notifications

import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.oceantech.tracking.core.TrackingViewModel
import com.oceantech.tracking.data.repository.NotificationRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject


class NotificationViewModel @AssistedInject constructor(
   @Assisted state: NotificationViewState,
   private val repository: NotificationRepository
) : TrackingViewModel<NotificationViewState, NotificationViewAction, NotificationViewEvents>(state) {
    override fun handle(action: NotificationViewAction) {
        when (action) {
            NotificationViewAction.GetNotifications -> handleGetNotification()
        }
    }

    private fun handleGetNotification() {
        setState { copy(getNotification = Loading()) }
        repository.getNotifications().execute {
            copy(getNotification = it)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(state: NotificationViewState): NotificationViewModel
    }

    companion object : MavericksViewModelFactory<NotificationViewModel, NotificationViewState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: NotificationViewState
        ): NotificationViewModel? {
            val factory = when (viewModelContext) {
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
            }
            return factory?.create(state) ?: error("You must implement your activity/ fragment to NotificationViewModel.Factory")
        }
    }
}