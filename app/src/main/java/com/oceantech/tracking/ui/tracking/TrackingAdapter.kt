package com.oceantech.tracking.ui.tracking

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oceantech.tracking.R
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.UserItemBinding

class TrackingAdapter(
    private val mlistTracking: MutableList<Tracking>
    //private val action: (User) -> Unit
) :
    RecyclerView.Adapter<TrackingAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: UserItemBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackingAdapter.ViewHolder {
        val binding = UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackingAdapter.ViewHolder, position: Int) {
        holder.binding.textUser.text=mlistTracking[position]?.date
    }

    override fun getItemCount(): Int {
        return mlistTracking.size
    }

}