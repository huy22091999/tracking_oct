package com.oceantech.tracking.ui.tracking

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.android.material.textview.MaterialTextView
import com.oceantech.tracking.R
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.databinding.ItemTrackingBinding
import com.oceantech.tracking.utils.formatDate
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date

class TrackingAdapter(
    private val trackings:List<Tracking>,
    private val context: Context,
    private val showMenu:(View,Tracking)-> Unit
) : RecyclerView.Adapter<TrackingAdapter.TrackingHolder>(){
    class TrackingHolder(val context: Context, val binding: ViewBinding):RecyclerView.ViewHolder(binding.root){
        fun bindHolder(tracking: Tracking, showMenu:(View,Tracking)-> Unit) {
            with(binding as ItemTrackingBinding){
                trackingContent.text = tracking.content
                trackingDate.text = formatDate(tracking.date.toString())
                circleImageView.setOnClickListener {
                    showMenu(itemView.findViewById(R.id.circleImageView),tracking)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: TrackingHolder, position: Int) {
        holder.bindHolder(trackings[position], showMenu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackingHolder {
        val view:ViewBinding = ItemTrackingBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TrackingHolder(context,view)
    }

    override fun getItemCount(): Int = trackings.size
}