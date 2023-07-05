package com.oceantech.tracking.ui.home

import com.oceantech.tracking.core.NimpeViewEvents
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.data.model.User

sealed class HomeViewEvent:NimpeViewEvents{
    object ResetLanguege: HomeViewEvent()
    data class ReturnUpdateTracking(val content:String, val id:Int): HomeViewEvent()
    data class ReturnDetailUser(val user:User):HomeViewEvent()
    object ReturnListUsers : HomeViewEvent()
    object ReturnAddTracking: HomeViewEvent()
    object ReturnTracking: HomeViewEvent()
    object Logout: HomeViewEvent()
    data class ReturnUpdateInfo(val user: User):HomeViewEvent()
    data class ReturnNextUpdate(val user: User):HomeViewEvent()
}