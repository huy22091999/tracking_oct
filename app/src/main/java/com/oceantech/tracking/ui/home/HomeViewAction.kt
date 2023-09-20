package com.oceantech.tracking.ui.home

import com.oceantech.tracking.core.NimpeViewModelAction

sealed class HomeViewAction:NimpeViewModelAction{

    object GetItemTablayout:HomeViewAction()
    object RemoveItemTablayout:HomeViewAction()
    object GetCurrentUser:HomeViewAction()
    object ResetLang:HomeViewAction()
}