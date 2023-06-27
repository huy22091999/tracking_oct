package com.oceantech.tracking.ui.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class TestViewModel @Inject constructor() : ViewModel() {

    fun test() = "This is test"

}