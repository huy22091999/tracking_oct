package com.oceantech.tracking.ui.information

import com.oceantech.tracking.core.NimpeViewModelAction
import com.oceantech.tracking.data.model.User
import okhttp3.MultipartBody
import okhttp3.RequestBody

sealed class InformationViewActon : NimpeViewModelAction {
    data class UpdateUserAction(val user: User) : InformationViewActon()
    data class UploadImage(val image: MultipartBody.Part) : InformationViewActon()
}
