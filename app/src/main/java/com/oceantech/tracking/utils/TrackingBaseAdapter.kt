package com.oceantech.tracking.utils

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class TrackingBaseAdapter<VB: ViewBinding, E: Any>: RecyclerView.Adapter<TrackingBaseAdapter<VB, E>.ViewHolder>() {

    var list: List<E> = listOf()


    inner class ViewHolder(private val _binding: VB): RecyclerView.ViewHolder(_binding.root){
        val binding: VB
            get() = _binding

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            getBinding(parent)
        )
    }

    abstract fun getBinding(parent: ViewGroup): VB


    override fun getItemCount(): Int {
        return list.size
    }


}

internal class ItemDecoration(
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

