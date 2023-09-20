package com.oceantech.tracking.ui.home

import com.oceantech.tracking.core.NimpeViewEvents
//done
sealed class HomeViewEvent:NimpeViewEvents{
    data class ChangeDarkMode(var isCheckedDarkMode: Boolean) : HomeViewEvent()
    object Logout : HomeViewEvent()

}