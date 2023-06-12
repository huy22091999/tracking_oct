package com.oceantech.tracking.ui.trackings

import com.oceantech.tracking.data.model.Tracking

interface OnClickTracking {
    fun onClick(tracking: Tracking)
}