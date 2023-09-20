package com.oceantech.tracking.ui.tracking.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.databinding.ItemTrackingBinding
//done
class TrackingAdapter(val onClick: (Tracking) -> Unit): RecyclerView.Adapter<TrackingAdapter.TrackingViewHolder>() {
    var mList: List<Tracking> = listOf()

    fun setData(data: List<Tracking>){
        mList=data
        notifyDataSetChanged()
    }
    inner class TrackingViewHolder(val binding:ItemTrackingBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(tracking: Tracking){
            binding.trackingContent.text="${tracking.content}"
            binding.time.text="${tracking.date}"
            binding.itemTracking.setOnClickListener {
                onClick(tracking)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackingViewHolder {
        val binding=ItemTrackingBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TrackingViewHolder(binding)
    }

    override fun getItemCount(): Int =mList.size

    override fun onBindViewHolder(holder: TrackingViewHolder, position: Int) {
        val tracking=mList[position]
        holder.bind(tracking)
    }
}