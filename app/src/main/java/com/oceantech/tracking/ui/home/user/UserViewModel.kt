package com.oceantech.tracking.ui.home.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.oceantech.tracking.data.network.UserApi
import com.oceantech.tracking.utils.UserPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import javax.inject.Inject

//@Suppress("UNCHECKED_CAST")
@HiltViewModel
class UserViewModel @Inject constructor(
    val api: UserApi
) : ViewModel() {
    fun loadUsers() = Pager(
        PagingConfig(pageSize = 5)
    ) {
        UserPagingSource(api)
    }.flow.cachedIn(viewModelScope)

}