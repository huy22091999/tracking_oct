package com.oceantech.tracking.ui.home

import com.airbnb.mvrx.*
import com.oceantech.tracking.core.TrackingViewModel
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.repository.TimeSheetRepository
import com.oceantech.tracking.data.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class HomeViewModel @AssistedInject constructor(
    // Tham số constructor @Assisted được Dagger cung cấp giá trị thông qua Dependency Injection.
    @Assisted state: HomeViewState,
    val userRepository: UserRepository, // Phụ thuộc vào lớp UserRepository để lấy dữ liệu về người dùng.
    val timeSheetRepository: TimeSheetRepository
) : TrackingViewModel<HomeViewState, HomeViewAction, HomeViewEvent>(state) {
    // Biến language để lưu giữ trạng thái ngôn ngữ hiện tại của màn hình "Home".
    var language: Int = 1
    override fun handle(action: HomeViewAction) {
        when (action) {
            is HomeViewAction.GetCurrentUser -> handleCurrentUser()
            is HomeViewAction.ResetLang -> handResetLang()
            is HomeViewAction.UpdateMyself -> handleEditProfile(action.user)
            is HomeViewAction.GetAllTimeSheet -> handleGetAllTimeSheetByUser()
            is HomeViewAction.CheckIn -> handleCheckIn(action.ip)
        }
    }

    private fun handleCheckIn(ip:String) {
        setState {
            this.copy(timeSheet = Loading())
        }
        timeSheetRepository.checkIn(ip).execute {
            copy(timeSheet= it)
        }
    }

    private fun handleGetAllTimeSheetByUser() {
        setState {
            this.copy(timeSheets = Loading())
        }
        timeSheetRepository.getAllByUser().execute {
            copy(timeSheets = it)
        }
    }

    fun removeTimeSheet(){
        setState {
            this.copy(timeSheet = Uninitialized)
        }
    }
    private fun handResetLang() {
        _viewEvents.post(HomeViewEvent.ResetLanguege)
    }
    private fun handleCurrentUser() {
        setState { copy(userCurrent = Loading()) }
        userRepository.getCurrentUser().execute {
            copy(userCurrent = it)
        }
    }
    private fun handleEditProfile(user: User){
        setState { copy(userCurrent = Loading()) }
        userRepository.updateMyself(user).execute {
            copy(userCurrent = it)
        }
    }
    // Factory interface để tạo HomeViewModel, được đánh dấu bằng @AssistedFactory.
    @AssistedFactory
    interface Factory {
        fun create(initialState: HomeViewState): HomeViewModel
    }

    // Companion object kế thừa từ MvRxViewModelFactory để cho biết ViewModel có khả năng tạo ra các instance của chính nó thông qua factory.
    companion object : MvRxViewModelFactory<HomeViewModel, HomeViewState> {
        // Phương thức create được ghi đè từ MvRxViewModelFactory. Được gọi bởi MvRx framework để tạo ViewModel.
        @JvmStatic
        override fun create(
            viewModelContext: ViewModelContext,
            state: HomeViewState
        ): HomeViewModel {
            // Tìm Factory tương ứng từ ViewModelContext để tạo ViewModel mới với trạng thái ban đầu đã cho.
            val factory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }
            // Nếu không tìm thấy Factory tương ứng, sẽ thông báo lỗi yêu cầu activity/fragment implement Factory interface để cung cấp ViewModel.
            return factory?.create(state)
                ?: error("You should let your activity/fragment implements Factory interface")
        }
    }
}