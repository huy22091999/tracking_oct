package com.oceantech.tracking.ui.profile

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingClickItem
import com.oceantech.tracking.data.model.Menu2
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.ItemDetailProfileBinding
import com.oceantech.tracking.utils.StringUltis.dateFormat
import com.oceantech.tracking.utils.StringUltis.dateIso8601Format
import com.oceantech.tracking.utils.StringUltis.dateIso8601Format2
import com.oceantech.tracking.utils.convertToStringFormat

class InfoAdapter(private val callback: TrackingClickItem) : RecyclerView.Adapter<InfoAdapter.ViewHolder>() {

    var list: ArrayList<Menu2>? = null


    @SuppressLint("NotifyDataSetChanged")
    fun setDataUser(user: User){
        var listTemp: ArrayList<Menu2> = ArrayList()

        listTemp.add(Menu2(0, R.drawable.baseline_beenhere_24, if (user.active == true) "active" else "no active"))
        listTemp.add(Menu2(1, R.drawable.ic_email, user.email))
        listTemp.add(Menu2(2, R.drawable.baseline_school_24, user.university))
        listTemp.add(Menu2(3, R.drawable.baseline_elevator_24, user.year?.toString()))
        listTemp.add(Menu2(4, R.drawable.baseline_transgender_24, if(user.gender == "M") "Male" else if (user.gender == "L") "Female" else null))
        listTemp.add(Menu2(5, R.drawable.baseline_calendar_today_24, user.dob?.convertToStringFormat(dateIso8601Format2, dateFormat)))
        listTemp.add(Menu2(6, R.drawable.baseline_place_24, user.birthPlace))

        list = listTemp
        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: ArrayList<Menu2>?) {
        if (list != null){
            this.list = list
            notifyDataSetChanged()
        }
    }



    inner class ViewHolder(private val binding: ViewBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(detailInfo: Menu2){
            with(binding as ItemDetailProfileBinding){
                binding.imgIcon.setImageResource(detailInfo.icon)
                binding.tvBody.text = detailInfo.text ?: binding.root.context.getString(R.string.nodata)

                binding.root.setOnClickListener{
                    callback.onItemMenu2ClickListenner(detailInfo)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    = ViewHolder(ItemDetailProfileBinding.inflate(LayoutInflater.from(parent.context), parent , false))

    override fun getItemCount(): Int {
        if (list!= null) return list!!.size
        return 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list != null){
            holder.onBind(list!![position])
        }
    }
}