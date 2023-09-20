package com.oceantech.tracking.ui.security
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
//done
class SecurityViewModel @AssistedInject constructor(
    @Assisted state: SecurityViewState,
    val repository: AuthRepository,
    private val userRepo:UserRepository
) :
    TrackingViewModel<SecurityViewState,SecurityViewAction,SecurityViewEvent>(state) {

    init {
        handleCurrentUser()
    }

    override fun handle(action: SecurityViewAction) {
        when(action){
            is SecurityViewAction.SignupAction->handleSignup(action.user)
            is SecurityViewAction.LoginAction->handleLogin(action.userName,action.password)
            is SecurityViewAction.SaveTokenAction->handleSaveToken(action.token)
        }
    }

    private fun handleCurrentUser() {
        setState { copy(userCurrent=Loading()) }
        userRepo.getCurrentUser().execute {
            copy(userCurrent=it)
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
    private fun handleSignup(user:User){
        setState {
            copy(userCurrent=Loading())
        }
        repository.signup(user).execute {
            copy(userCurrent= it)

        }
    }
    private fun handleSaveToken(tokenResponse: TokenResponse)
    {
        this.viewModelScope.async {
            repository.saveAccessTokens(tokenResponse)
        }

    }

    fun handleReturnSignUp(user: User) {
        _viewEvents.post(SecurityViewEvent.ReturnSignUpEvent(user))
    }
    fun handleReturnInfoRegister() {
        _viewEvents.post(SecurityViewEvent.ReturnInfoRegisterEvent)
    }
    fun handleReturnLogin() {
        _viewEvents.post(SecurityViewEvent.ReturnLoginEvent)
    }
    fun handleReturnResetPass() {
        _viewEvents.post(SecurityViewEvent.ReturnResetPassEvent)
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