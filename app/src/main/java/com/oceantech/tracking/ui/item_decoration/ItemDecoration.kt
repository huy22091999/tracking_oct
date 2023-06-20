package com.oceantech.tracking.ui.item_decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemDecoration(
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