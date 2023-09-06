package com.oceantech.tracking.ui.security
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.*
import com.oceantech.tracking.core.TrackingViewModel
import com.oceantech.tracking.data.model.TokenResponse
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.repository.AuthRepository
import com.oceantech.tracking.data.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.async


class SecurityViewModel @AssistedInject constructor(
    @Assisted state: SecurityViewState,
    val repository: AuthRepository,
    private val userRepo:UserRepository
) :
    TrackingViewModel<SecurityViewState,SecurityViewAction,SecurityViewEvent>(state) {
    init {

    }

    override fun handle(action: SecurityViewAction) {
        when(action){
            is SecurityViewAction.LogginAction->handleLogin(action.userName,action.password)
            is SecurityViewAction.SignAction->handleRegister(action.user)
            is SecurityViewAction.SaveTokenAction->handleSaveToken(action.token)
            is SecurityViewAction.GetUserCurrent ->handleCurrentUser()
        }
    }

    private fun handleCurrentUser() {
        setState { copy(userCurrent=Loading()) }
        userRepo.getCurrentUser().execute {
            copy(userCurrent=it)
        }
    }
    private fun handleRegister(user: User) {
        // Perform the registration logic
        setState { copy(asyncRegister = Loading()) }

        // Call repository to perform registration
        repository.register(user).execute {
            copy(asyncRegister = it)
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
    private fun handleSaveToken(tokenResponse: TokenResponse)
    {
        this.viewModelScope.async {
            repository.saveAccessTokens(tokenResponse)
        }

    }

    fun handleReturnSignin() {
        _viewEvents.post(SecurityViewEvent.ReturnSigninEvent)
    }
    fun handleReturnResetPass() {
        _viewEvents.post(SecurityViewEvent.ReturnResetpassEvent)
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