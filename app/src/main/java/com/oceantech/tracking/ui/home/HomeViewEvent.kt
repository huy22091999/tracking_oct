package com.oceantech.tracking.ui.home

import com.oceantech.tracking.core.NimpeViewEvents

sealed class HomeViewEvent:NimpeViewEvents{
    data class ResetLanguage(val language: String):HomeViewEvent()
    object SaveFeedback:HomeViewEvent()
}