package com.oceantech.tracking.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.airbnb.mvrx.*
import com.oceantech.tracking.core.TrackingViewModel
import com.oceantech.tracking.data.model.TimeSheet
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.repository.TimeSheetRepository
import com.oceantech.tracking.data.repository.TrackingRepository
import com.oceantech.tracking.data.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class HomeViewModel @AssistedInject constructor(
    @Assisted state: HomeViewState,
    val repository: UserRepository,
    private val trackingRepo: TrackingRepository,
    private val timeSheetRepo:TimeSheetRepository
) : TrackingViewModel<HomeViewState, HomeViewAction, HomeViewEvent>(state) {
    var language: Int = 1

    init {
        handleAllTracking()
        handleTimeSheets()
        handleAllUsers()
        handleCurrentUser()
    }
    override fun handle(action: HomeViewAction) {
        when (action) {
            is HomeViewAction.GetCurrentUser -> handleCurrentUser()
            is HomeViewAction.ResetLang -> handResetLang()
            is HomeViewAction.GetTrackings -> handleAllTracking()
            is HomeViewAction.GetTimeSheets -> handleTimeSheets()
            is HomeViewAction.GetCheckIn -> handleCheckIn(action.ip)
            is HomeViewAction.SaveTracking -> handleSaveTracking(action.content)
            is HomeViewAction.UpdateTracking -> handleUpdateTracking(action.id, action.content)
            is HomeViewAction.DeleteTracking -> handleDeleteTracking(action.id)
            is HomeViewAction.GetAllUsers -> handleAllUsers()
            is HomeViewAction.BlockUser -> handleBlockUser(action.id)
            is HomeViewAction.EditTokenDevice -> handleTokenDevice(action.tokenDevice)
            is HomeViewAction.UpdateMyself -> handleUpdateMyself(action.user)
            is HomeViewAction.EditUser -> handleEditUser(action.user)
        }
    }

    private fun handleEditUser(user: User) {
        setState { copy(asyncEditUser = Loading()) }
        repository.edit(user).execute {
            copy(asyncEditUser = it)
        }
    }

    private fun handleUpdateMyself(user: User) {
        setState { copy(asyncUpdateMySelf = Loading()) }
        repository.updateMyself(user).execute {
            copy(asyncUpdateMySelf = it)
        }
    }

    private fun handleTokenDevice(tokenDevice: String) {
        setState { copy(asyncTokenDevice = Loading()) }
        repository.edit(tokenDevice).execute {
            copy(asyncTokenDevice = it)
        }
    }

    fun handleRemoveStateBlockUser() = setState { copy(asyncBlockUser = Uninitialized) }
    private fun handleBlockUser(id: Int) {
        setState { copy(asyncBlockUser = Loading()) }
        repository.blockUser(id).execute {
            copy(asyncBlockUser = it)
        }
    }

    fun handleRemoveStateAllUsers() = setState { copy(allUsers = Uninitialized) }
    private fun handleAllUsers() {
        setState { copy(allUsers = Loading()) }
        repository.getAllUser().execute {
            copy(allUsers = it)
        }
    }

    private fun handleCheckIn(ip:String) {
        setState { copy(checkIn = Loading()) }
        timeSheetRepo.checkIn(ip).execute {
            copy(checkIn = it)
        }
    }

    private fun handleDeleteTracking(id:Int) {
        setState { copy(asyncDeleteTracking = Loading()) }
        trackingRepo.delete(id).execute {
            copy(asyncDeleteTracking = it)
        }
    }

    private fun handleUpdateTracking(id:Int, content:String) {
        setState { copy(asyncUpdateTracking = Loading()) }
        trackingRepo.update(id, content).execute {
            copy(asyncUpdateTracking = it)
        }
    }

    fun handleTimeSheets() {
        setState { copy(timeSheets = Loading()) }
        timeSheetRepo.getAllByUser().execute {
            copy(timeSheets = it)
        }
    }
    fun handleAllTracking() {
        setState { copy(allTracking = Loading()) }
        trackingRepo.getAllByUser().execute {
            copy(allTracking = it)
        }
    }

    private fun handleSaveTracking(content:String) {
        setState { copy(asyncSaveTracking = Loading()) }
        trackingRepo.save(content).execute {
            copy(asyncSaveTracking = it)
        }
    }
    private fun handResetLang() {
        _viewEvents.post(HomeViewEvent.ResetLanguege)
    }
    fun handleReturnUpdate(content:String, id:Int){
        _viewEvents.post(HomeViewEvent.ReturnUpdateTracking(content, id))
    }

    fun handleRemoveStateOfAdd(){
        setState {
            copy(asyncSaveTracking = Uninitialized)
        }
    }
    fun handleRemoveStateOfUpdate(){
        setState {
            copy(asyncUpdateTracking = Uninitialized)
        }
    }
    fun handleRemoveStateOfDelete(){
        setState {
            copy(asyncDeleteTracking = Uninitialized)
        }
    }
    fun handleRemoveStateOfCheckIn(){
        setState {
            copy(checkIn = Uninitialized)
        }
    }
    private fun handleCurrentUser() {
        setState { copy(userCurrent = Loading()) }
        repository.getCurrentUser().execute {
            copy(userCurrent = it)
        }
    }

    fun handleReturnTracking(){
        _viewEvents.post(HomeViewEvent.ReturnTracking)
    }

    fun handleReturnDetailUser(user: User){
        _viewEvents.post(HomeViewEvent.ReturnDetailUser(user))
    }

    fun handleReturnUsers() {
        _viewEvents.post(HomeViewEvent.ReturnListUsers)
    }

    fun handleReturnUpdateInfo(user: User){
        _viewEvents.post(HomeViewEvent.ReturnUpdateInfo(user))
    }

    fun handleNextUpdateInfo(user: User){
        _viewEvents.post(HomeViewEvent.ReturnNextUpdate(user))
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