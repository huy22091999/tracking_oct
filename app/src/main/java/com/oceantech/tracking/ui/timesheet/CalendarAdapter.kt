package com.oceantech.tracking.ui.timesheet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.oceantech.tracking.R
import com.oceantech.tracking.databinding.DayItemBinding

class CalendarAdapter(private val days:Map<Int, Boolean>, private val context:Context):RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
        val binding = DayItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val layoutParams = binding.root.layoutParams
        layoutParams.height = parent.height / 7

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val key = days.keys.elementAt(position)
        val value = days[key]
        holder.onBind(value!!,key, context)
    }

    override fun getItemCount(): Int {
        return days.size
    }
    inner class ViewHolder(private val binding:ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(hasCheckIn:Boolean, day:Int, context:Context){
            with(binding as DayItemBinding){
                if(day > 0){
                    if(hasCheckIn){
                        binding.dayTextView.text = day.toString()
                        binding.dayTextView.background = context.getDrawable(R.color.teal_200)
                    } else {
                        binding.dayTextView.text = day.toString()
                    }
                } else {
                    binding.dayTextView.text = ""
                }
            }
        }
    }
}