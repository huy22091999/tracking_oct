package com.oceantech.tracking.ui.home

import com.oceantech.tracking.core.NimpeViewModelAction
import com.oceantech.tracking.data.model.Feedback

sealed class HomeViewAction:NimpeViewModelAction{

    object GetCurrentUser:HomeViewAction()
    object GetCategorys:HomeViewAction()
    object ResetLang:HomeViewAction()
    data class SaveFeedback(val feedback: Feedback):HomeViewAction()

    object GetAllUsers: HomeViewAction()

}