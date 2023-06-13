package com.oceantech.tracking.ui.trackings

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.databinding.ItemTrackingBinding
import java.text.SimpleDateFormat

class TrackingAdapter(val listener : OnClickTracking) : RecyclerView.Adapter<TrackingAdapter.TrackingViewHolder>() {

    private var list : List<Tracking> = listOf()
    class TrackingViewHolder(val binding: ItemTrackingBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackingViewHolder {
        val binding = ItemTrackingBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TrackingViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: TrackingViewHolder, position: Int) {
        val tracking = list[position]
        holder.binding.content.text = tracking.content
        holder.binding.date.text = tracking.date?.let { SimpleDateFormat("dd-MM-yyyy").format(it) }
        holder.itemView.setOnClickListener {
            listener.onClick(tracking)
        }
    }
    fun setData(listData : MutableList<Tracking>){
        list = listData
        notifyDataSetChanged()
    }
}