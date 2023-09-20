package com.oceantech.tracking.ui.tracking

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.oceantech.tracking.R
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.databinding.TrackingItemBinding
import com.oceantech.tracking.utils.convertToDateTimePartsList

class TrackingAdapter(
    private val context: Context,
    private val mlistTracking: MutableList<Tracking>,
    private val action: (Tracking, Int) -> Unit
) :
    RecyclerView.Adapter<TrackingAdapter.ViewHolder>() {

    private var mDateList: List<Pair<String, String>> =
        emptyList() // Sử dụng Pair để lưu cặp ngày tháng và giờ phút

    private fun updateDataLists() {
        try {
            val dateStrings = mlistTracking.mapNotNull { it.date }
            mDateList = dateStrings.convertToDateTimePartsList()
        } catch (e: Exception) {
            println("Có lỗi khi chuyển đổi định dạng")
        }
    }

    inner class ViewHolder(
        val binding: TrackingItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(tracking: Tracking) {
            binding.tvDate.text = mDateList[adapterPosition].first
            binding.tvHour.text = mDateList[adapterPosition].second
            binding.tvContent.text = tracking.content
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            TrackingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        updateDataLists()
        if (!mDateList.isNullOrEmpty()) {
            val tracking = mlistTracking[position]
            holder.onBind(tracking)
            holder.binding.imgEdit.setOnClickListener {
                val itemPosition = holder.adapterPosition
                if (itemPosition != RecyclerView.NO_POSITION) {
                    val item = mlistTracking[itemPosition]
                    showPopupMenu(holder, item, itemPosition)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mlistTracking.size
    }

    private fun showPopupMenu(holder: ViewHolder, tracking: Tracking, itemPosition: Int) {
        val popupMenu = PopupMenu(context, holder.binding.imgEdit)
        popupMenu.inflate(R.menu.menu_edit)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.item_edit -> {
                    Toast.makeText(context, "This is Edit", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.item_delete -> {
                    Toast.makeText(context, "This is Delete", Toast.LENGTH_SHORT).show()
                    action(tracking, itemPosition)

                    true
                }

                else -> {
                    false
                }
            }
        }
        popupMenu.show()
    }

    fun removeItem(position: Int) {
        mlistTracking.removeAt(position)
        notifyItemRemoved(position)
    }

    fun restoreOriginalList() {
        notifyDataSetChanged()
    }
}
