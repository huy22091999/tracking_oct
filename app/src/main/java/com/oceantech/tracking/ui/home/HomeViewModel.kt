package com.oceantech.tracking.ui.home

import android.annotation.SuppressLint
import androidx.appcompat.widget.SwitchCompat
import com.airbnb.mvrx.*
import com.oceantech.tracking.core.TrackingBaseViewModel
import com.oceantech.tracking.data.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.util.Random

class HomeViewModel @AssistedInject constructor(
    @Assisted state: HomeViewState,
    val repository: UserRepository,
) : TrackingBaseViewModel<HomeViewState, HomeViewAction, HomeViewEvent>(state) {
    var language: Int = 1
    var positionTabSelected = 0
    override fun handle(action: HomeViewAction) {
        when (action) {
            is HomeViewAction.GetItemTablayout -> handleItemTablayout()
            is HomeViewAction.RemoveItemTablayout -> handleRemoveItemTablayout()
            is HomeViewAction.GetCurrentUser -> handleCurrentUser()
            is HomeViewAction.ResetLang -> handResetLang()
        }
    }
    private fun handResetLang() {
        _viewEvents.post(HomeViewEvent.ResetLanguege)
    }
    @SuppressLint("CheckResult")
    private fun handleItemTablayout() {
        setState { copy(itemTabLayout = Loading()) }
        repository.getItemTablayout().execute{
            copy(itemTabLayout = it)
        }
    }

    private fun handleRemoveItemTablayout() {
        setState { copy(itemTabLayout = Uninitialized) }
    }
    private fun handleCurrentUser() {
        setState { copy(userCurrent = Loading()) }
        repository.getCurrentUser().execute {
            copy(userCurrent = it)
        }
    }

    fun returnEventSwitchMode(isBoolean: Boolean) {
        _viewEvents.post(HomeViewEvent.handleSwitchMode(isBoolean))
    }
    fun returnEventChangeLanguage(lang: String) {
        _viewEvents.post(HomeViewEvent.handleChangeLanguage(lang))
    }

    fun returnEventLogout() {
        _viewEvents.post(HomeViewEvent.logoutEvent)
    }



    @AssistedFactory
    interface Factory {
        fun create(initialState: HomeViewState): HomeViewModel
    }

    companion object : MvRxViewModelFactory<HomeViewModel, HomeViewState> {
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