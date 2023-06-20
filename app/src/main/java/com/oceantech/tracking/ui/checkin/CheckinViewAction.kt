package com.oceantech.tracking.ui.checkin

import com.oceantech.tracking.core.NimpeViewModelAction

sealed class CheckinViewAction : NimpeViewModelAction{
    data class Checkin(var ip : String) : CheckinViewAction()
    object GetIp : CheckinViewAction()
    object GetTimeSheet : CheckinViewAction()
}