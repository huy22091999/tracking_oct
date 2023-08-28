package com.oceantech.tracking.ui.tracking

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.oceantech.tracking.R
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.databinding.ItemTrackingBinding
import com.oceantech.tracking.utils.StringUltis
import com.oceantech.tracking.utils.convertToStringFormat
import timber.log.Timber

@SuppressLint("NotifyDataSetChanged", "SetTextI18n")
class TrackingAdapter(private val callBack : (view: View, tracking: Tracking) -> Unit) : RecyclerView.Adapter<TrackingAdapter.ViewHolder>() {

    private var listTracking: ArrayList<Tracking>? = null

    fun setAllData(list: ArrayList<Tracking>) {
        this.listTracking = list
        notifyDataSetChanged()
    }

    fun addItemData(tracking: Tracking) {
        if (listTracking != null){
            this.listTracking!!.add(tracking)
            notifyDataSetChanged()
        }

    }

    fun updateItemData(tracking: Tracking) {
        if (listTracking != null){
            for (i in 0 until listTracking!!.size){
                if (listTracking!![i].id == tracking.id){
                    listTracking!!.add(i, tracking)
                    notifyItemChanged(i)
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
                    notifyItemChanged(i)
                    break
                }
            }
        }
    }


    inner class ViewHolder(private val callBack : (view : View, tracking: Tracking) -> Unit, val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun onBin(tracking: Tracking){
            with(binding as ItemTrackingBinding){
                Timber.e(tracking.date)
                binding.itemTvTime.text = tracking.date?.convertToStringFormat(StringUltis.dateIso8601Format, StringUltis.dateTimeHourFormat)
                binding.itemTvDay.text = tracking.date?.convertToStringFormat(StringUltis.dateIso8601Format, StringUltis.dateDayFormat)
                binding.itemTvContent.text = tracking.content

                binding.itemTvOption.setOnClickListener{ callBack( binding.itemTvOption, tracking)}
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(callBack, ItemTrackingBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        if (listTracking != null) return listTracking!!.size
        return 10
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (listTracking != null) holder.onBin(listTracking!![position])
    }
}