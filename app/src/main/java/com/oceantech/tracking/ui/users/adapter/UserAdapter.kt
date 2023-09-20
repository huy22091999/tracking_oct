package com.oceantech.tracking.ui.users.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.ItemUserBinding
//done
class UserAdapter(private val action: (User) -> Unit) :
    PagingDataAdapter<User, UserAdapter.UserViewHolder>(userComparator) {

    inner class UserViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.fullName.text = " ${user.displayName}"
            binding.email.text = " ${ user.email }"
            binding.year.text =" ${ user.year}"
            binding.countDayCheckIn.text = user.countDayCheckin.toString()
            binding.countDayTracking.text = user.countDayTracking.toString()
            binding.itemView.setOnClickListener {
                action(user)
            }
        }
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding: ItemUserBinding =
            ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    companion object {
        private val userComparator = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem == newItem
            }

        }
    }
}