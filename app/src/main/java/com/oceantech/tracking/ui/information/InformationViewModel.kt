package com.oceantech.tracking.ui.information

import com.airbnb.mvrx.ActivityViewModelContext
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.ViewModelContext
import com.oceantech.tracking.core.TrackingViewModel
import com.oceantech.tracking.data.model.UpLoadImage
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//ViewModels là nơi tồn tại tất cả logic
class InformationViewModel @AssistedInject constructor(
    @Assisted state: InformationViewState,
    private val userRepo: UserRepository
) : TrackingViewModel<InformationViewState, InformationViewActon, InformationViewEvent>(state) {
    override fun handle(action: InformationViewActon) {
        when (action) {
            is InformationViewActon.UpdateUserAction -> handleUpdateUser(action.user)
            is InformationViewActon.UploadImage -> handleUploadImage(action.image)
        }
    }

    fun restartState() = setState {
        copy(user = Uninitialized, upLoadImage = Uninitialized)
    }

    private fun handleUploadImage(image: MultipartBody.Part) {

    }


    private fun handleUpdateUser(user: User) {
        setState { copy(user = Loading()) }
        userRepo.updateUser(user).execute {
            copy(user = it)
        }
    }

    public fun handleReturnEditUser() {
        _viewEvents.post(InformationViewEvent.ReturnEditEvent)
    }

    @AssistedFactory
    interface Factory {
        fun create(initialState: InformationViewState): InformationViewModel
    }

    companion object : MvRxViewModelFactory<InformationViewModel, InformationViewState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: InformationViewState
        ): InformationViewModel {
            val fatory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }
            return fatory?.create(state)
                ?: error("You should let your activity/fragment implements Factory interface")
        }
    }
}