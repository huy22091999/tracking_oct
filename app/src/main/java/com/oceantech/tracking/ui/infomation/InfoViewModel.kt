package com.oceantech.tracking.ui.infomation

import com.airbnb.mvrx.*
import com.oceantech.tracking.core.TrackingBaseViewModel
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class InfoViewModel @AssistedInject constructor(
    @Assisted state: InfoViewsState,
    private val userRepo : UserRepository
) :
    TrackingBaseViewModel<InfoViewsState, InfoViewsAction, InfoViewsEvent>(state) {

    override fun handle(action: InfoViewsAction) {
        when(action){
            is InfoViewsAction.GetUserAction -> handleGetUser();
            is InfoViewsAction.UpdateUserAction -> handleUpdateUser(action.user);
        }
    }

    private fun handleGetUser() {
        setState { copy(user = Loading()) }
        userRepo.getCurrentUser().execute {
            copy(user = it)
        }
    }

    private fun handleUpdateUser(user: User) {
        setState { copy(user = Loading()) }
        userRepo.updateUser(user).execute {
            copy(user = it)
        }
    }

    public fun handleReturnEditUser(){
        _viewEvents.post(InfoViewsEvent.ReturnEditEvent)
    }


    @AssistedFactory
    interface Factory {
        fun create(initialState: InfoViewsState): InfoViewModel
    }

    companion object : MvRxViewModelFactory<InfoViewModel, InfoViewsState>{
        override fun create(
            viewModelContext: ViewModelContext,
            state: InfoViewsState
        ): InfoViewModel {
            val fatory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }
            return fatory?.create(state)
                ?: error("You should let your activity/fragment implements Factory interface")
        }
    }
}