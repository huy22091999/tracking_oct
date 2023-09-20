package com.oceantech.tracking.ui.tracking

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.oceantech.tracking.R

import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.databinding.TrackingItemBinding
import com.oceantech.tracking.utils.StringUltis
import com.oceantech.tracking.utils.convertToDateTimePartsList


class TrackingAdapter(
    private val context: Context,
    private val mlistTracking: MutableList<Tracking>
    //private val action: (User) -> Unit
) :
    RecyclerView.Adapter<TrackingAdapter.ViewHolder>() {

    private var mDateList: List<Pair<String, String>> =
        emptyList() // Sử dụng Pair để lưu cặp ngày tháng và giờ phút


    private fun updateDataLists() {
        val dateStrings = mlistTracking.mapNotNull { it.date }
        Toast.makeText(context, mlistTracking.size.toString(), Toast.LENGTH_SHORT).show()
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
        updateDataLists()
        if (!mDateList.isEmpty()) {
            holder.binding.tvDate.text = mDateList[position].first
            holder.binding.tvHour.text = mDateList[position].second
            holder.binding.tvContent.text = mlistTracking[position].content
        }

        holder.binding.imgEdit.setOnClickListener {
            popupMenus(it)
        }
    }

    private fun popupMenus(v: View) {
        val popupMenu = PopupMenu(context, v)
        popupMenu.inflate(R.menu.menu_edit)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.item_edit -> {
                    Toast.makeText(context, "This is Edit", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.item_delete -> {
                    Toast.makeText(context, "This is Delete", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> {
                    false
                }
            }
        }
        popupMenu.show()
    }

    override fun getItemCount(): Int {
        return mlistTracking.size
    }

    fun removeItem(position: Int) {
        mlistTracking.removeAt(position)
        notifyItemRemoved(position)
    }

    fun restoreOriginalList() {
        notifyDataSetChanged()
    }


}