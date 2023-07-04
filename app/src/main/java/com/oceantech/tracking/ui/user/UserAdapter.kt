package com.oceantech.tracking.ui.user

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.oceantech.tracking.R
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.ItemUserBinding

class UserAdapter (private val context: Context,
                   private val users:List<User>,
                   private val action:(User) ->Unit
    ):RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    class UserViewHolder(private val context: Context, private val binding:ViewBinding):RecyclerView.ViewHolder(binding.root){
        fun onBind(user: User){
            with(binding as ItemUserBinding){
                binding.university.text = "${context.getString(R.string.university)}: ${user.university} - ${context.getString(R.string.year)} ${user.year}"
                binding.email.text = "${context.getString(R.string.email)}: ${user.email}"
                binding.fullName.text = "${user.firstName} ${user.lastName}"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder{
        val view = ItemUserBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return UserViewHolder(context, view)
    }

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.onBind(user = users[position])
        holder.itemView.setOnClickListener {
            Log.i("selected item:", users[position].email.toString())
            action(users[position])
        }
    }
}