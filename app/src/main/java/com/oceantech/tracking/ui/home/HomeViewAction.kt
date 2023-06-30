package com.oceantech.tracking.data.home

import com.oceantech.tracking.core.NimpeViewModelAction
import com.oceantech.tracking.data.model.Feedback
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.data.model.User

sealed class HomeViewAction:NimpeViewModelAction{

    object GetCurrentUser: HomeViewAction()
    object GetTrackings: HomeViewAction()
    object GetTimeSheets: HomeViewAction()
    object GetAllUsers: HomeViewAction()
    data class GetCheckIn(val ip:String): HomeViewAction()
    object ResetLang: HomeViewAction()
    object ResetTheme: HomeViewAction()
    data class SaveTracking(val content:String): HomeViewAction()
    data class UpdateTracking(val id:Int, val content:String): HomeViewAction()
    data class DeleteTracking(val id:Int): HomeViewAction()

}