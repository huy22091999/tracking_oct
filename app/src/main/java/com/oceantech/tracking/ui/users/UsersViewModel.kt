package com.oceantech.tracking.ui.users

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.ViewModelContext
import com.oceantech.tracking.core.TrackingViewModel
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UsersViewModel @AssistedInject constructor(
    @Assisted state: UserViewState, private val userRepo: UserRepository
) : TrackingViewModel<UserViewState, UsersViewAction, UsersViewEvent>(state) {
    private var job: Job? = null
    override fun handle(action: UsersViewAction) {
        when (action) {
            is UsersViewAction.RefeshUserAction -> handleFetchData(action.lifecycleScope)
            is UsersViewAction.EditUser -> handleEditInfo(action.id, action.user)
            is UsersViewAction.blockUser -> handleBlock(action.id)
            is UsersViewAction.restart -> handleResart()
            else -> {}
        }
    }

    fun handleRemoveStateBlockUser() =
        setState { copy(blockUser = Uninitialized, userEdit = Uninitialized) }

    private fun handleBlock(id: Int) {
        setState { copy(blockUser = Loading()) }
        userRepo.blockUser(id).execute {
            copy(blockUser = it)
        }
    }

    private fun handleEditInfo(id: Int, user: User) {
        setState { copy(userEdit = Loading()) }
        userRepo.edit(id, user).execute {
            copy(userEdit = it)
        }
    }

    private fun handleResart() {
        setState { copy(userEdit = Uninitialized) }
    }

    private fun handleFetchData(lifecycleScope: LifecycleCoroutineScope) {
        setState { copy(pageUsers = Loading()) }
        job?.cancel()
        job = lifecycleScope.launch {
            userRepo.getUserByPage().cachedIn(viewModelScope).collectLatest {
                setState { copy(pageUsers = Success(it)) }
            }
        }
    }

    fun handleReturnDetailUser(user: User) {
        _viewEvents.post(UsersViewEvent.ReturnDetailViewEvent(user))
    }

    fun handleReturnEditInfo(user: User) {
        _viewEvents.post(UsersViewEvent.ReturnEditInfo(user))
    }

    @AssistedFactory
    interface Factory {
        fun create(initialState: UserViewState): UsersViewModel
    }

    companion object : MvRxViewModelFactory<UsersViewModel, UserViewState> {
        override fun create(
            viewModelContext: ViewModelContext, state: UserViewState
        ): UsersViewModel {
            val fatory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as Factory
                is ActivityViewModelContext -> viewModelContext.activity as Factory
            }
            return fatory.create(state)
        }
    }
}