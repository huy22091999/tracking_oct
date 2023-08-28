package com.oceantech.tracking.ui.users

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oceantech.tracking.R
import com.oceantech.tracking.data.model.User

class UserAdapter(
    private val action:(User) ->Unit
) : PagingDataAdapter<User, UserAdapter.ViewHolder>(USER_COMPARATOR) {

    companion object{
        private val USER_COMPARATOR = object : DiffUtil.ItemCallback<User>() {          // callback này sẽ so sánh 2 item trc và mới có trùng nhau
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                return  oldItem == newItem
            }

        }
    }

    inner class ViewHolder(private val itemView: View, private val action:(User) ->Unit) : RecyclerView.ViewHolder(itemView) {
        private var img = itemView.findViewById<android.widget.ImageView?>(R.id.item_user_img)
        private var tvName = itemView.findViewById<TextView?>(R.id.item_user_name)
        private var item_main = itemView.findViewById<RelativeLayout?>(R.id.item_main)

        fun onBind(user: User?){
            Glide.with(itemView.context).load("user.link").placeholder(R.drawable.ic_person).into(img);
            tvName.text = user?.displayName

            item_main.setOnClickListener{
                action(user!!)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false),
            action)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}