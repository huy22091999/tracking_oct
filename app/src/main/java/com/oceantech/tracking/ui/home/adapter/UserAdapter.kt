package com.oceantech.tracking.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.UserItemBinding

class UserAdapter: RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var listUsers: List<User> = emptyList()
    class UserViewHolder(private val _binding: UserItemBinding): RecyclerView.ViewHolder(_binding.root){
        val binding: UserItemBinding
            get() = _binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    fun setListUsers(listUsers: List<User>){
        this.listUsers = listUsers
    }
    override fun getItemCount(): Int {
        return listUsers.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = listUsers[position]
        holder.binding.user = user
    }
}