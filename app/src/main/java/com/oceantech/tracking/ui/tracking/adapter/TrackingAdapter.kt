package com.oceantech.tracking.ui.tracking.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.databinding.TrackingItemBinding

class TrackingAdapter: RecyclerView.Adapter<TrackingAdapter.TrackingViewHolder>() {

    private var listTracking: List<Tracking> = emptyList()

    class TrackingViewHolder(private val _binding: TrackingItemBinding): RecyclerView.ViewHolder(_binding.root){
        val binding: TrackingItemBinding
            get() = _binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackingViewHolder {
        return TrackingViewHolder(
            TrackingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return listTracking.size
    }

    override fun onBindViewHolder(holder: TrackingViewHolder, position: Int) {
        val tracking = listTracking[position]
        holder.binding.tracking = tracking
    }

    fun setListTracking(listTracking: List<Tracking>){
        this.listTracking = listTracking
    }
}