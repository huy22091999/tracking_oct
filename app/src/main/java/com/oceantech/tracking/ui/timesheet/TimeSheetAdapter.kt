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
import java.text.SimpleDateFormat
import java.util.Date

class TimeSheetAdapter(private val timeSheets:List<TimeSheet>):RecyclerView.Adapter<TimeSheetAdapter.TimeSheetHolder>() {
    class TimeSheetHolder(private val binding:ViewBinding):RecyclerView.ViewHolder(binding.root){
        fun bindHolder(timeSheet:TimeSheet){
            with(binding as ItemTimeSheetBinding){
                val date: Date = formatDate(timeSheet.dateAttendance.toString())
                val formatterDate = SimpleDateFormat("dd/MM/yyyy - HH:mm")
                binding.timeLabel.text = formatterDate.format(date)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSheetHolder
        = TimeSheetHolder(ItemTimeSheetBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount(): Int{
        if (timeSheets.size >= 3) return 3
        return timeSheets.size
    }

    override fun onBindViewHolder(holder: TimeSheetHolder, position: Int){
        if (timeSheets.size > 0){
            return holder.bindHolder(timeSheets[position])
        }
    }
}