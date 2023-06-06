package com.oceantech.tracking.ui.home

import com.oceantech.tracking.core.NimpeViewModelAction
import com.oceantech.tracking.data.model.Feedback
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.data.model.User

sealed class HomeViewAction:NimpeViewModelAction{

    object GetCurrentUser:HomeViewAction()
    object GetCategorys:HomeViewAction()
    object GetTrackings:HomeViewAction()
    object ResetLang:HomeViewAction()
    data class SaveTracking(val content:String):HomeViewAction()
    data class UpdateTracking(val content:String, val id:Int):HomeViewAction()
    data class DeleteTracking(val id:Int):HomeViewAction()
    data class SaveFeedback(val feedback: Feedback):HomeViewAction()
}