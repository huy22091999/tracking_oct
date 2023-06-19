package com.oceantech.tracking.ui.checkin

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.oceantech.tracking.data.model.TimeSheet
import com.oceantech.tracking.databinding.ItemTimeSheetBinding
import java.text.SimpleDateFormat
import java.util.Calendar

class CheckinAdapter : RecyclerView.Adapter<CheckinAdapter.CheckinViewHolder>() {
    private var listTimeSheet : List<TimeSheet> = listOf()
    class CheckinViewHolder(val binding : ItemTimeSheetBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckinViewHolder {
        val binding = ItemTimeSheetBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CheckinViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listTimeSheet.size
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onBindViewHolder(holder: CheckinViewHolder, position: Int) {
        val timeSheet = listTimeSheet[position]
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val sDate = timeSheet.dateAttendance?.let { sdf.format(it) }
        val calendar = Calendar.getInstance()
        if (timeSheet.dateAttendance != null) {
            calendar.time = timeSheet.dateAttendance
        }
        val day = calendar.get(Calendar.DAY_OF_WEEK)
        holder.binding.tvTime.text = "Thứ $day \n $sDate"
    }
    fun setData(list: List<TimeSheet>){
        listTimeSheet = list
    }
}