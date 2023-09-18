package com.oceantech.tracking.ui.users

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oceantech.tracking.R
import com.oceantech.tracking.data.model.User

val USER_COMPARATOR = object : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean =
        // User ID serves as unique ID
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean =
        // Compare full contents (note: Java users should call .equals())
        oldItem == newItem
}

class UserAdapter(private val action: (User) -> Unit) :
    PagingDataAdapter<User, UserAdapter.ViewHolder>(USER_COMPARATOR) {

    inner class ViewHolder(private val itemView: View, private val action: (User) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private var imgUser = itemView.findViewById<ImageView?>(R.id.imageView)
        private var userName = itemView.findViewById<TextView?>(R.id.text_user)
        private var email = itemView.findViewById<TextView?>(R.id.text_email)


        fun onBind(user: User?) {

            Glide.with(itemView.context).load("user.link").placeholder(R.drawable.ic_person)
                .into(imgUser)
            userName.text = user?.displayName
            email.text = user?.email
            itemView.setOnClickListener {
                action(user!!)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val repoItem = getItem(position)
        if (repoItem != null)
            holder.onBind(repoItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false),
            action
        )
    }


}