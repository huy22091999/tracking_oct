package com.oceantech.tracking.ui.home.user.adapter

import android.text.Layout
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.UserItemBinding
import com.oceantech.tracking.utils.TrackingBaseAdapter
import com.oceantech.tracking.utils.showToast

class UserAdapter(
    private val showUserInformation: (User) -> Unit
): TrackingBaseAdapter<UserItemBinding, User>(){

    private var authority: String = ""

    fun setAuthority(authority: String){
        this.authority = authority
    }

    override fun getBinding(parent: ViewGroup): UserItemBinding {
        return UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = list[position]
        holder.binding.user = user
        if (authority == "ROLE_ADMIN"){
            holder.binding.root.apply {
                setOnClickListener {
                    showUserInformation(user)
                }
            }
        }
    }
}