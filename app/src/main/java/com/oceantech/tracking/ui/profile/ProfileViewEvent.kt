package com.oceantech.tracking.ui.profile

import com.oceantech.tracking.core.NimpeViewEvents


sealed class ProfileViewEvent:NimpeViewEvents{
    object ResetLanguege: ProfileViewEvent()
}
