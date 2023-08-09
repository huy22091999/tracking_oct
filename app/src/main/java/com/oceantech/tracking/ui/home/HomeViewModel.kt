package com.oceantech.tracking.ui.home

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.airbnb.mvrx.*
import com.oceantech.tracking.core.TrackingViewModel
import com.oceantech.tracking.data.model.PageSearch
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.network.UserApi
import com.oceantech.tracking.data.repository.UserRepository
import com.oceantech.tracking.utils.UserPagingSource
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single

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
            is HomeViewAction.LockUser -> handleLockUser(action.id)
            is HomeViewAction.UpdateMyself -> handleUpdateMyself(action.user)
            is HomeViewAction.UpdateUser -> handleUpdateUser(action.user, action.id)
            is HomeViewAction.GetUser -> handleGetUser(action.id)
            is HomeViewAction.SearchByPage -> handleSearchPage(action.pageSearch)
            is HomeViewAction.InitPage -> handleInitPage()
        }
    }

    private fun handleInitPage() {
        setState { copy(initPage = Loading()) }
        repository.searchByPage(
            PageSearch(
                pageIndex = 1,
                size = 5
            )
        ).execute {
            copy(initPage = it)
        }
    }

    fun handleFlowData(): Flow<PagingData<User>> = Pager(
        PagingConfig(pageSize = 5)
    ) {
        UserPagingSource(repository)
    }.flow.cachedIn(viewModelScope)

    private fun handleSearchPage(pageSearch: PageSearch) {
        setState { copy(searchPage = Loading()) }
        repository.searchByPage(pageSearch).execute {
            copy(searchPage = it)
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

    private fun handleUpdateUser(user: User, id: Int) {
        setState { copy(updateUser = Loading()) }
        repository.updateUser(user, id).execute {
            copy(updateUser = it)
        }
    }

    private fun handleUpdateMyself(user: User) {
        setState { copy(updateMyself = Loading()) }
        repository.updateMyself(user).execute {
            copy(updateMyself = it)
        }
    }

    private fun handleLockUser(id: Int) {
        setState { copy(lockUser = Loading()) }
        repository.lockUser(id).execute {
            copy(lockUser = it)
        }
    }



    private fun handleGetUser(id: Int) {
        setState { copy(getUser = Loading()) }
        repository.getUser(id).execute {
            copy(getUser = it)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(initialState: HomeViewState): HomeViewModel
    }

    companion object : MavericksViewModelFactory<HomeViewModel, HomeViewState> {
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