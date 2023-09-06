package com.oceantech.tracking.ui.profile

import com.oceantech.tracking.data.repository.UserRepository
import com.oceantech.tracking.ui.home.HomeViewState
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class ProfileViewModel @AssistedInject constructor(
    @Assisted state: ProfileViewState,
    val repository: UserRepository,
) {
}