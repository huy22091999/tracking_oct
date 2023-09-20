package com.oceantech.tracking.ui.home

import androidx.appcompat.widget.SwitchCompat
import com.oceantech.tracking.core.NimpeViewEvents

sealed class HomeViewEvent:NimpeViewEvents{
    object ResetLanguege:HomeViewEvent()
    object logoutEvent:HomeViewEvent()
    data class handleSwitchMode(val isDarkMode: Boolean): HomeViewEvent()
    data class handleChangeLanguage(val language: String): HomeViewEvent()
    object SaveFeedback:HomeViewEvent()
}