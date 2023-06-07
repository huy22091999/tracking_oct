package com.oceantech.tracking.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.ItemUserBinding

class HomeAdapter : RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    private var listUser : List<User> = listOf()

    class HomeViewHolder(val binding : ItemUserBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return HomeViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listUser.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val user = listUser[position]
        holder.binding.displayName.text = user.displayName
        holder.binding.email.text = user.email
    }
    fun setData(list: List<User>){
        listUser = list
    }
}