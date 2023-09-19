package com.oceantech.tracking.ui.tracking

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.databinding.TrackingItemBinding
import com.oceantech.tracking.utils.StringUltis
import com.oceantech.tracking.utils.convertToCalendarDay
import com.oceantech.tracking.utils.convertToDateTimePartsList


class TrackingAdapter(
    private val mlistTracking: MutableList<Tracking>
    //private val action: (User) -> Unit
) :
    RecyclerView.Adapter<TrackingAdapter.ViewHolder>() {

    private var mDateList: List<Pair<String, String>> = emptyList() // Sử dụng Pair để lưu cặp ngày tháng và giờ phút

    init {
        updateDataLists()
    }

    private fun updateDataLists() {
        val dateStrings = mlistTracking.mapNotNull { it.date }
        mDateList = dateStrings.convertToDateTimePartsList(StringUltis.dateIso8601Format)
    }



    inner class ViewHolder(val binding: TrackingItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackingAdapter.ViewHolder {
        val binding =
            TrackingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackingAdapter.ViewHolder, position: Int) {
        if (!mDateList.isEmpty()) {
            holder.binding.tvDate.text = mDateList[position].first
            holder.binding.tvHour.text = mDateList[position].second
        }
    }

    override fun getItemCount(): Int {
        return mlistTracking.size
    }

}