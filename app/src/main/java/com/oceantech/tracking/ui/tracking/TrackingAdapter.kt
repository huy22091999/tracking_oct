package com.oceantech.tracking.ui.tracking

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.oceantech.tracking.R
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.databinding.ItemAddTrackingBinding
import com.oceantech.tracking.databinding.ItemTrackingBinding
import com.oceantech.tracking.core.TrackingClickItem
import com.oceantech.tracking.databinding.ItemAddMoreTrackingBinding
import com.oceantech.tracking.utils.StringUltis
import com.oceantech.tracking.utils.convertToStringFormat

@SuppressLint("NotifyDataSetChanged", "SetTextI18n")
class TrackingAdapter(private val callBack: TrackingClickItem) : RecyclerView.Adapter<TrackingAdapter.ViewHolder>() {

   val TYPE_ITEM_ADD = 0
   val TYPE_ITEM_TRACKING = 1
   val TYPE_ITEM_ADD_MORE_TRACKING = 2

    private var listTracking: ArrayList<Tracking>? = null

    fun setAllData(list: ArrayList<Tracking>) {
        this.listTracking = list
        notifyDataSetChanged()
    }

    fun addItemData(tracking: Tracking) {
        if (listTracking != null){
            this.listTracking!!.add(tracking)
            notifyItemChanged(listTracking!!.size - 1)
        }

    }

    fun updateItemData(tracking: Tracking) {
        if (listTracking != null){
            for (i in 0 until listTracking!!.size){
                if (listTracking!![i].id == tracking.id){
                    listTracking!!.removeAt(i)
                    listTracking!!.add(i, tracking)
                    notifyItemChanged(i+1)
                    break
                }
            }
        }
    }

    fun deleteItemData(tracking: Tracking) {
        if (listTracking != null){
            for (i in 0 until listTracking!!.size){
                if (listTracking!![i].id == tracking.id){
                    listTracking!!.removeAt(i)
                    notifyItemChanged(i+1)
                    break
                }
            }
        }
    }

    fun findItemPositoon(tracking: Tracking){
        if (listTracking != null){
            for (i in 0 until listTracking!!.size){
                if (listTracking!![i].id == tracking.id){
                    callBack.onItemPosition(i+1)
                    break
                }
            }
        }
    }


    inner class ViewHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun onBinTracking(tracking: Tracking){
            with(binding as ItemTrackingBinding){
                binding.itemTvOption.isVisible = true
                binding.itemTvTime.text = tracking.date?.convertToStringFormat(StringUltis.dateIso8601Format, StringUltis.dateTimeHourFormat)
                binding.itemTvDay.text = tracking.date?.convertToStringFormat(StringUltis.dateIso8601Format, StringUltis.dateDayFormat)
                binding.itemTvContent.text = tracking.content ?: binding.itemTvContent.context.getString(R.string.nodata)
                binding.itemTvOption.setOnClickListener{ callBack.onItemTrackingOptionMenuClickListenner(binding.itemTvOption, tracking) }
            }
        }
        fun onBinAddTracking(){
            with(binding as ItemAddTrackingBinding){
                binding.tilAdd.setOnClickListener{callBack.onItemTrackingAddClickListenner() }

                binding.cvAvt.setOnClickListener{callBack.onItemAvatarClickListennner()}
            }
        }
        fun onBinAddMoreTracking(){
            with(binding as ItemAddMoreTrackingBinding){
                binding.tvMessage.text = binding.tvMessage.context.getString(R.string.no_more)
                binding.btnAdd.text = binding.tvMessage.context.getString(R.string.tracking_now)
                binding.btnAdd.setOnClickListener{callBack.onItemTrackingAddClickListenner() }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) return TYPE_ITEM_ADD
        if (position > (listTracking?.size ?: 10)) return TYPE_ITEM_ADD_MORE_TRACKING
        return TYPE_ITEM_TRACKING
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == TYPE_ITEM_ADD)
            return ViewHolder(ItemAddTrackingBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        if (viewType == TYPE_ITEM_ADD_MORE_TRACKING)
            return ViewHolder(ItemAddMoreTrackingBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        return ViewHolder(ItemTrackingBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        if (listTracking != null) return listTracking!!.size + 2
        return 10
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder.itemViewType == TYPE_ITEM_ADD) holder.onBinAddTracking()
        else if ((holder.itemViewType == TYPE_ITEM_ADD_MORE_TRACKING)) holder.onBinAddMoreTracking()
        else if ((holder.itemViewType == TYPE_ITEM_TRACKING))
            if (listTracking != null) holder.onBinTracking(listTracking!![position - 1])

    }
}