package com.oceantech.tracking.ui.tracking.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class TrackingItemDecoration(
    private val distance: Int
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = distance
        outRect.top = distance
        outRect.right = distance
        outRect.left = distance
    }
}