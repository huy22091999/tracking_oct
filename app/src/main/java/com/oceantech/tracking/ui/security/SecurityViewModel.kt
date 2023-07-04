package com.oceantech.tracking.ui.security

import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.*
import com.oceantech.tracking.core.TrackingViewModel
import com.oceantech.tracking.data.model.TokenResponse
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.repository.AuthRepository
import com.oceantech.tracking.data.repository.PublicRepository
import com.oceantech.tracking.data.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.async


class SecurityViewModel @AssistedInject constructor(
    @Assisted state: SecurityViewState,
    val repository: AuthRepository,
    private val userRepo: UserRepository,
    private val publicRepo: PublicRepository
) :
    TrackingViewModel<SecurityViewState,SecurityViewAction,SecurityViewEvent>(state) {
    init {

    }

    override fun handle(action: SecurityViewAction) {
        when(action){
            is SecurityViewAction.LogginAction->handleLogin(action.userName,action.password)
            is SecurityViewAction.SaveTokenAction->handleSaveToken(action.token)
            is SecurityViewAction.SignAction -> handleSign(action.user)
            is SecurityViewAction.GetUserCurrent ->handleCurrentUser()
            is SecurityViewAction.GetConfigApp -> handleConfigApp()
        }
    }

    private fun handleConfigApp() {
        setState { copy(asyncConfigApp = Loading()) }
        publicRepo.getConfigApp().execute {
            copy(asyncConfigApp = it)
        }
    }

    private fun handleCurrentUser() {
        setState { copy(userCurrent=Loading()) }
        userRepo.getCurrentUser().execute {
            copy(userCurrent=it)
        }
    }

    private fun handleSign(user:User) {
        setState {
            copy(asyncSign = Loading())
        }
        userRepo.sign(user).execute {
            copy(asyncSign = it)
        }
    }

    private fun handleLogin(userName:String,password: String){
        setState {
            copy(asyncLogin=Loading())
        }
        repository.login(userName,password).execute {
            copy(asyncLogin=it)
        }
    }

    fun handleRemoveStateError(){
        setState {
            copy(asyncLogin = Uninitialized)
        }
    }

    private fun handleSaveToken(tokenResponse: TokenResponse)
    {
        this.viewModelScope.async {
            repository.saveAccessTokens(tokenResponse)
        }
    }

    fun handleReturnSignin() {
        setState {
            copy(asyncSign = Uninitialized)
        }
        _viewEvents.post(SecurityViewEvent.ReturnSigninEvent)
    }
    fun handleReturnResetPass() {
        _viewEvents.post(SecurityViewEvent.ReturnResetpassEvent)
    }

    fun handleReturnLogin(){
        setState {
            copy(asyncLogin = Uninitialized)
        }
        _viewEvents.post(SecurityViewEvent.ReturnLoginEvent)
    }

    fun handleReturnNextSignIn(user:User){
        _viewEvents.post(SecurityViewEvent.ReturnNextSignInEvent(user))
    }

    fun getString()="test"
    @AssistedFactory
    interface Factory {
        fun create(initialState: SecurityViewState): SecurityViewModel
    }

    companion object : MvRxViewModelFactory<SecurityViewModel, SecurityViewState> {
        @JvmStatic
        override fun create(viewModelContext: ViewModelContext, state: SecurityViewState): SecurityViewModel {
            val factory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }

            return factory?.create(state) ?: error("You should let your activity/fragment implements Factory interface")
        }
    }
}