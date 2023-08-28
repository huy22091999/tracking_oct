package com.oceantech.tracking.ui.users

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.oceantech.tracking.core.TrackingViewModel
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.network.UsersPagingSource
import com.oceantech.tracking.data.repository.UserRepository
import com.oceantech.tracking.ui.home.HomeViewAction
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow

class UserViewModel @AssistedInject constructor(
   @Assisted state: UserViewState,
    private val repository: UserRepository
    ):TrackingViewModel<UserViewState,UserViewAction,UserViewEvent>(state) {
    var language: Int = 1
    override fun handle(action: UserViewAction) {
       when(action){
           is UserViewAction.ResetLang -> handResetLang()
           is UserViewAction.GetListUser -> handleGetListUser()
           is UserViewAction.RemoveUser -> handleRemoveUser()
           is UserViewAction.UpdateUser -> handleUpdateUser()
       }
    }

    private fun handResetLang() {
        _viewEvents.post(UserViewEvent.ResetLanguege)
    }

    private fun handleUpdateUser() {

    }

    private fun handleRemoveUser() {

    }

     fun handleGetListUser(): Flow<PagingData<User>>{
        val userData=repository.getAllUser().cachedIn(viewModelScope)
        return userData
    }

    fun handleReturnDetailUser(user: User) {

    }

    @AssistedFactory
    interface Factory{
        fun create(initialState: UserViewState): UserViewModel
    }

    companion object: MvRxViewModelFactory<UserViewModel,UserViewState>{
        @JvmStatic
        override fun create(
            viewModelContext: ViewModelContext,
            state: UserViewState
        ): UserViewModel {
            val factory=when(viewModelContext){
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }
            return factory?.create(state)?: error("You should let your activity/fragment implements Factory interface")
        }
    }

}