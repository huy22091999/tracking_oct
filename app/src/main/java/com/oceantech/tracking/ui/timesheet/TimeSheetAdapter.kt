package com.oceantech.tracking.ui.timesheet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.oceantech.tracking.data.model.TimeSheet
import com.oceantech.tracking.databinding.ItemTimeSheetBinding
import com.oceantech.tracking.utils.formatDate

class TimeSheetAdapter(private val timeSheets:List<TimeSheet>):RecyclerView.Adapter<TimeSheetAdapter.TimeSheetHolder>() {
    class TimeSheetHolder(val binding:ViewBinding):RecyclerView.ViewHolder(binding.root){
        fun bindHolder(timeSheet:TimeSheet){
            with(binding as ItemTimeSheetBinding){
                binding.timeLabel.text = formatDate(timeSheet.dateAttendance.toString())
                binding.ipLabel.text = timeSheet.ip
                binding.noteLabel.text = timeSheet.note
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSheetHolder
        = TimeSheetHolder(ItemTimeSheetBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount(): Int
        = timeSheets.size

    override fun onBindViewHolder(holder: TimeSheetHolder, position: Int) = holder.bindHolder(timeSheets[position])
}