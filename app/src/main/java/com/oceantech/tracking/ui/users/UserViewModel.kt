package com.oceantech.tracking.ui.users

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.airbnb.mvrx.*
import com.oceantech.tracking.core.TrackingBaseViewModel
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UserViewModel @AssistedInject constructor(
    @Assisted state: UserViewState,
    private val userRepo: UserRepository
) : TrackingBaseViewModel<UserViewState, UsersViewAction, UsersViewEvent>(state) {

    private var job: Job? = null


    override fun handle(action: UsersViewAction) {
        when (action) {
            is UsersViewAction.RefeshUserAction -> handleFetchData(action.lifecycleScope)
        }
    }

    private fun handleFetchData(lifecycleScope: LifecycleCoroutineScope) {
        setState { copy(pageUsers = Loading()) }
        job?.cancel()
        job = lifecycleScope.launch {
            userRepo
                .getUserByPage()
                .cachedIn(viewModelScope)
                .collectLatest {
                    setState { copy(pageUsers = Success(it)) }
                }
        }
    }

    fun getString() = "test"

    @AssistedFactory
    interface Factory {
        fun create(initialState: UserViewState): UserViewModel
    }

    companion object : MvRxViewModelFactory<UserViewModel, UserViewState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: UserViewState
        ): UserViewModel? {
            val fatory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }
            return fatory?.create(state)
                ?: error("You should let your activity/fragment implements Factory interface")
        }
    }
}