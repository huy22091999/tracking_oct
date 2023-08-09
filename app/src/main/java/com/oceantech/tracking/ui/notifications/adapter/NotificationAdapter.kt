package com.oceantech.tracking.ui.notifications.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.oceantech.tracking.data.model.Notification
import com.oceantech.tracking.databinding.NotificationItemBinding
import com.oceantech.tracking.utils.TrackingBaseAdapter

class NotificationAdapter: TrackingBaseAdapter<Notification>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = list[position]
        (holder.binding as NotificationItemBinding).notification = notification
    }

    override fun getBinding(parent: ViewGroup, viewType: Int): ViewBinding {
        return NotificationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }
}