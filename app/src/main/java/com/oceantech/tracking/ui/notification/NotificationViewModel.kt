package com.oceantech.tracking.ui.notification

import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.oceantech.tracking.core.TrackingViewModel
import com.oceantech.tracking.data.repository.NotificationRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class NotificationViewModel @AssistedInject constructor(
    @Assisted state: NotificationViewState,
    val notificationRepo: NotificationRepository
) : TrackingViewModel<NotificationViewState, NotificationViewAction, NotificationViewEvent>(state) {
    override fun handle(action: NotificationViewAction) {
        when (action) {
            is NotificationViewAction.getAllNotification -> handlGetNotification()
        }
    }

    private fun handlGetNotification() {
        setState { copy(mListnotification = Loading()) }
        notificationRepo.getAllByUser().execute {
            copy(mListnotification = it)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(initialState: NotificationViewState): NotificationViewModel
    }

    companion object : MvRxViewModelFactory<NotificationViewModel, NotificationViewState> {
        @JvmStatic
        override fun create(
            viewModelContext: ViewModelContext,
            state: NotificationViewState
        ): NotificationViewModel? {
            val factory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? NotificationViewModel.Factory
                is ActivityViewModelContext -> viewModelContext.activity as? NotificationViewModel.Factory
            }
            return factory?.create(state)
                ?: error("You should let your activity/fragment implements Factory interface")
        }
    }

}