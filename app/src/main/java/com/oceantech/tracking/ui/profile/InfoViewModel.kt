package com.oceantech.tracking.ui.profile

import android.util.Log
import com.airbnb.mvrx.*
import com.oceantech.tracking.core.TrackingBaseViewModel
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.repository.AuthRepository
import com.oceantech.tracking.data.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class InfoViewModel @AssistedInject constructor(
    @Assisted state: InfoViewsState,
    private val userRepo : UserRepository,
    private val authRepo : AuthRepository
) :
    TrackingBaseViewModel<InfoViewsState, InfoViewsAction, InfoViewsEvent>(state) {

    override fun handle(action: InfoViewsAction) {
        when(action){
            is InfoViewsAction.GetMyUserAction -> handleGetMyUser();
            is InfoViewsAction.CheckIsAdmin -> checkIsAdmin(action.user);
            is InfoViewsAction.GetUserCurentAction -> handleGetUser();
            is InfoViewsAction.GetUserCurentByID -> handleSetUser(action.userId, action.isMyProfile);
            is InfoViewsAction.UpdateUserAction -> handleUpdateUser(action.user);
            is InfoViewsAction.BlockUser -> blockUser();
            is InfoViewsAction.UnblockUser -> unblockUser();
            is InfoViewsAction.RemoveUpdateUserAction -> removeUpdateUser();

            is InfoViewsAction.VerifyUserAction -> handleVerifyUser(action.userName,action.pasword);
        }
    }

    private fun handleGetMyUser() {
        setState { copy(myUserUser = Loading()) }
        userRepo.getCurrentUser().execute {
            copy(myUserUser = it, isAdmin = false)
        }
    }
    private fun checkIsAdmin(user: User) {
        setState { copy(isAdmin = user.roles!![0].authority == "ROLE_ADMIN") }
    }

    private fun handleSetUser(userId: String?, isMyProfile: Boolean) {
        if (userId != null){
            setState { copy(userEdit = Loading(), isMyProfile = isMyProfile) }
            userRepo.getCurrentUserById(userId).execute {
                copy(userEdit = it)
            }
        }else{
            setState { copy(userEdit = Fail(Throwable()), isMyProfile = isMyProfile) }
        }

    }

    private fun handleGetUser() {
        setState { copy(userEdit = Loading(), isMyProfile = true) }
        userRepo.getCurrentUser().execute {
            copy(userEdit = it)
        }
    }


    private fun handleVerifyUser(userName: String?, password: String?) {
        setState { copy(userVerify = Loading()) }
        authRepo.login(userName, password).execute {
            copy(userVerify = it)
        }
    }

    private fun handleUpdateUser(user: User) {
        setState { copy(updateUser = Loading()) }
        withState{
            if(it.isMyProfile){
                userRepo.updateUser(user).execute {updateUser->
                    copy(updateUser = updateUser)
                }
            }else if(it.isAdmin){
                userRepo.updateUserById(user.id ?: "" ,user).execute {updateUser->
                    copy(updateUser = updateUser)
                }
            }else{
                setState { copy(updateUser = Fail(Throwable())) }
            }
        }

    }

    private fun blockUser() {
        setState { copy(updateUser = Loading()) }
        withState{
            if(it.userEdit.invoke()?.id != null && !it.isMyProfile && it.isAdmin ){
                userRepo.blockUser(it.userEdit.invoke()!!.id!!).execute {updateUser->
                    copy(updateUser = updateUser, userEdit = updateUser)
                }
            }else{
                setState { copy(updateUser = Fail(Throwable())) }
            }
        }
    }
    private fun unblockUser() {

        setState { copy(updateUser = Loading()) }
        withState{
            var user = it.userEdit.invoke()
            if(user?.id != null && !it.isMyProfile && it.isAdmin ){
                user.active = true
                userRepo.updateUserById(user.id!! ,user).execute {updateUser ->
                    copy(userEdit = updateUser, updateUser = updateUser)
                }
            }else{
                setState { copy(updateUser = Fail(Throwable())) }
            }
        }
    }

    private fun removeUpdateUser() {
        setState { copy(updateUser = Uninitialized) }
    }

    fun RetunNavigateToFrg(id: Int) {
        _viewEvents.post(InfoViewsEvent.ReturnNavigateToFrgViewEvent(id))
    }
    fun RetunBackToFrg() {
        _viewEvents.post(InfoViewsEvent.ReturnBacktoFrgViewEvent)
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