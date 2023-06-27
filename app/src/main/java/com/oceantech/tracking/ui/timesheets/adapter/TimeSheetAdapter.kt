package com.oceantech.tracking.ui.timesheets.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.oceantech.tracking.data.model.TimeSheet
import com.oceantech.tracking.databinding.TimeSheetItemBinding
import com.oceantech.tracking.utils.toLocalDate
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class TimeSheetAdapter : RecyclerView.Adapter<TimeSheetAdapter.TimeSheetViewHolder>() {

    private var listTimeSheet: List<TimeSheet> = mutableListOf()

    class TimeSheetViewHolder(private val _binding: TimeSheetItemBinding) :
        RecyclerView.ViewHolder(_binding.root) {
        val binding: TimeSheetItemBinding
            get() = _binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSheetViewHolder {
        return TimeSheetViewHolder(
            TimeSheetItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    fun setListTimeSheet(listTimeSheet: List<TimeSheet>) {
        val timeSheets = mutableListOf<TimeSheet>()
        listTimeSheet.onEach { timeSheet ->
            timeSheets.add(timeSheet.copy(dateAttendance = timeSheet.dateAttendance?.toLocalDate(timeSheet.dateAttendance)))
        }
        this.listTimeSheet = timeSheets
    }

    override fun getItemCount(): Int {
        return listTimeSheet.size
    }

    override fun onBindViewHolder(holder: TimeSheetViewHolder, position: Int) {
        val timeSheet = listTimeSheet[position]

        holder.binding.timesheet = timeSheet
    }
}