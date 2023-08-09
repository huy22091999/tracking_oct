package com.oceantech.tracking.ui.timesheets.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.oceantech.tracking.data.model.TimeSheet
import com.oceantech.tracking.databinding.TimeSheetItemBinding
import com.oceantech.tracking.utils.TrackingBaseAdapter
import com.oceantech.tracking.utils.toLocalDate
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class TimeSheetAdapter : TrackingBaseAdapter<TimeSheet>() {
    override fun getBinding(parent: ViewGroup, viewType: Int): ViewBinding {
        return TimeSheetItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    fun setListTimeSheet(listTimeSheet: List<TimeSheet>) {
        val timeSheets = mutableListOf<TimeSheet>()
        listTimeSheet.onEach { timeSheet ->
            timeSheets.add(
                timeSheet.copy(
                    dateAttendance = timeSheet.dateAttendance?.toLocalDate(
                        timeSheet.dateAttendance
                    )
                )
            )
        }
        list = timeSheets
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val timeSheet = list[position]
        (holder.binding as TimeSheetItemBinding).timesheet = timeSheet
    }

}