package com.oceantech.tracking.ui.profile

import com.oceantech.tracking.core.NimpeViewEvents


sealed class ProfileViewEvent : NimpeViewEvents {
    data class ResetLanguege(var lang: String) : ProfileViewEvent()
}
