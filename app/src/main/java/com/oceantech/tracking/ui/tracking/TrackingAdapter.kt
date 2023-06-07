package com.oceantech.tracking.ui.tracking

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.oceantech.tracking.R
import com.oceantech.tracking.data.model.Tracking
import java.util.Date

class TrackingAdapter(
    private val trackings:List<Tracking>,
) : RecyclerView.Adapter<TrackingAdapter.TrackingHolder>(){
    inner class TrackingHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        private val trackingDate:MaterialTextView = itemView.findViewById(R.id.tracking_date)
        private val trackingContent:MaterialTextView = itemView.findViewById(R.id.tracking_content)
        fun onBind(tracking: Tracking) {
            trackingContent.text = tracking.content
            trackingDate.text = tracking.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackingHolder = TrackingHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_tracking,parent,false))
    override fun getItemCount(): Int = trackings.size
    override fun onBindViewHolder(holder: TrackingHolder, position: Int){
        holder.onBind(trackings[position])
    }
}