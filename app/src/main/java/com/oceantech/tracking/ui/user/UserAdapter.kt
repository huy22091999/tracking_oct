package com.oceantech.tracking.ui.user

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.oceantech.tracking.R
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.ItemUserBinding

class UserAdapter (private val context: Context,
                   private val action:(User) ->Unit
    ):PagingDataAdapter<User, UserAdapter.UserViewHolder>(COMPARATOR) {
    class UserViewHolder(private val context: Context, private val binding:ViewBinding):RecyclerView.ViewHolder(binding.root){
        fun onBind(user: User){
            with(binding as ItemUserBinding){
                binding.email.text = "${context.getString(R.string.email)}: ${user.email}"
                binding.fullName.text = "${user.firstName} ${user.lastName}"
                binding.countDayCheckIn.text = user.countDayCheckin.toString()
                binding.countDayTracking.text = user.countDayTracking.toString()
                binding.levelLabel.text = "null"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder{
        val view = ItemUserBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return UserViewHolder(context, view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        getItem(position)?.let{ user ->
            holder.onBind(user = user)
            holder.itemView.setOnClickListener {
                action(user)
            }
        }
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean =
                oldItem == newItem

        }
    }
}