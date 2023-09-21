package com.oceantech.tracking.ui.notification

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.oceantech.tracking.data.model.Notification
import com.oceantech.tracking.databinding.NotificationItemBinding


class NotificationAdapter(
    private val context: Context,
    private val mlistNotification: MutableList<Notification>
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    inner class ViewHolder(
        val binding: NotificationItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(notification: Notification) {
            binding.txtTitle.text = notification.title
            binding.txtBody.text = notification.body
            binding.txtDate.text = notification.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = NotificationItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder((binding))
    }

    override fun getItemCount(): Int {
        if (mlistNotification.size != null)
            return mlistNotification.size
        return 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = mlistNotification[position]
        holder.onBind(notification)
    }
}