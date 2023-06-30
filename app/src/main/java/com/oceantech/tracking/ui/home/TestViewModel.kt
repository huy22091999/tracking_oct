package com.oceantech.tracking.data.home

import androidx.lifecycle.ViewModel
import javax.inject.Inject


class TestViewModel @Inject constructor() : ViewModel() {

    fun test() = "This is test"

}