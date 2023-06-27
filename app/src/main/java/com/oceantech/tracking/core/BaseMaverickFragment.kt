package com.oceantech.tracking.core

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.airbnb.mvrx.MavericksView

abstract class BaseMaverickFragment(@LayoutRes layoutId: Int  = 0): MavericksView, Fragment(layoutId) {


}