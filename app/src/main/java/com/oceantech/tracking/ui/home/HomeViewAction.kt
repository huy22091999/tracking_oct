package com.oceantech.tracking.ui.home

import com.oceantech.tracking.core.NimpeViewModelAction
import com.oceantech.tracking.data.model.Feedback
import com.oceantech.tracking.data.model.Tracking

sealed class HomeViewAction:NimpeViewModelAction{

    object GetCurrentUser:HomeViewAction()
    object GetAllUser : HomeViewAction()
    data class CheckIn(var ip : String) : HomeViewAction()
    object GetAllTimeSheet : HomeViewAction()
    data class AddTracking(var tracking: Tracking) : HomeViewAction()
    object GetCategorys:HomeViewAction()
    object ResetLang:HomeViewAction()
    data class SaveFeedback(val feedback: Feedback):HomeViewAction()

}