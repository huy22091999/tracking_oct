package com.oceantech.tracking.ui.users

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.ViewModelContext
import com.oceantech.tracking.core.TrackingViewModel
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
//done
class UserViewModel @AssistedInject constructor(
   @Assisted state: UserViewState,
    private val repository: UserRepository
    ):TrackingViewModel<UserViewState,UserViewAction,UserViewEvent>(state) {
    override fun handle(action: UserViewAction) {
       when(action){
           is UserViewAction.GetUserById -> handleGetUserById(action.id)
           is UserViewAction.GetListUser -> handleGetListUser()
           is UserViewAction.UpdateUser -> handleUpdateUser(action.user)
           is UserViewAction.BlockUserById -> handleBlockUserById(action.id)
           is UserViewAction.UnBlockUserById -> handleUpdateUser(action.user)
       }
    }

    fun removeUserState(){
        setState {
            this.copy(asyncUser = Uninitialized,
                asyncBlockUser= Uninitialized,
                asyncUpdateUser = Uninitialized)
        }
    }

    private fun handleBlockUserById(id: Int) {
        setState {
            this.copy(asyncBlockUser= Loading())
        }
        repository.blockUserById(id).execute {
            copy(asyncBlockUser=it, asyncUser = it)
        }
    }

    private fun handleGetUserById(id: Int) {
        setState {
            this.copy(asyncUser= Loading())
        }
        repository.getUserById(id).execute {
            copy(asyncUser= it)
        }
    }

    private fun handleUpdateUser(user: User) {
        setState {
            this.copy(asyncUpdateUser= Loading())
        }
        repository.updateUser(user).execute {
            copy(asyncUpdateUser=it, asyncUser = it)
        }
    }

    fun handleGetListUser(): Flow<PagingData<User>> {
        return repository.getAllUser().cachedIn(viewModelScope)
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