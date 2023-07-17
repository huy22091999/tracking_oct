package com.oceantech.tracking.ui.notifications.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.oceantech.tracking.data.model.Notification
import com.oceantech.tracking.databinding.NotificationItemBinding
import com.oceantech.tracking.utils.TrackingBaseAdapter

class NotificationAdapter: TrackingBaseAdapter<NotificationItemBinding, Notification>() {

    override fun getBinding(parent: ViewGroup): NotificationItemBinding {
        return NotificationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = list[position]
        holder.binding.notification = notification
    }
}