package com.oceantech.tracking.ui.security

import android.util.Log
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
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.async
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SecurityViewModel @AssistedInject constructor(
    @Assisted state: SecurityViewState,
    val repository: AuthRepository,
    private val userRepo: UserRepository
) :
    TrackingViewModel<SecurityViewState, SecurityViewAction, SecurityViewEvent>(state) {
    init {

    }

    override fun handle(action: SecurityViewAction) {
        when (action) {
            is SecurityViewAction.LogginAction -> handleLogin(action.userName, action.password)
            is SecurityViewAction.SaveTokenAction -> handleSaveToken(action.token)
            is SecurityViewAction.GetUserCurrent -> handleCurrentUser()
            is SecurityViewAction.SignInAction -> handleSignIn(action.userName, action.password, action.displayName)
            is SecurityViewAction.GetAllUsers -> handleAllUsers()
        }
    }

    private fun handleAllUsers() {
        setState {
            copy(allUsers = Loading())
        }
        userRepo.getAllUsers().execute {
            copy(allUsers = it)
        }
    }

    private fun handleCurrentUser() {
        setState { copy(userCurrent = Loading()) }
        userRepo.getCurrentUser().execute {
            copy(userCurrent = it)
        }
    }

    private fun handleLogin(userName: String, password: String) {
        setState {
            copy(asyncLogin = Loading())
        }
        repository.login(userName, password).execute {
            copy(asyncLogin = it)
        }
    }

    private fun handleSaveToken(tokenResponse: TokenResponse) {
        this.viewModelScope.async {
            repository.saveAccessTokens(tokenResponse)
        }
    }

    private fun handleSignIn(
        userName: String,
        password: String,
        displayName: String
    ) {
        setState {
            copy(userSignIn = Loading())
        }
        userRepo.createUpdateUser(userName, password, displayName).execute {
            copy(userSignIn = it)
        }

    }




    fun handleReturnSignin() {
        _viewEvents.post(SecurityViewEvent.ReturnSigninEvent)
    }

    fun handleReturnResetPass() {
        _viewEvents.post(SecurityViewEvent.ReturnResetpassEvent)
    }

    fun getString() = "test"

    @AssistedFactory
    interface Factory {
        fun create(initialState: SecurityViewState): SecurityViewModel
    }

    companion object : MvRxViewModelFactory<SecurityViewModel, SecurityViewState> {
        @JvmStatic
        override fun create(
            viewModelContext: ViewModelContext,
            state: SecurityViewState
        ): SecurityViewModel {
            val factory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }

            return factory?.create(state)
                ?: error("You should let your activity/fragment implements Factory interface")
        }
    }
}