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
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingClickItem
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.ItemAddTrackingBinding
import com.oceantech.tracking.databinding.ItemTitleBinding
import com.oceantech.tracking.databinding.ItemTrackingBinding
import com.oceantech.tracking.databinding.ItemUserBinding

class UserAdapter(
    private val callBack: TrackingClickItem
) : PagingDataAdapter<User, UserAdapter.ViewHolder>(USER_COMPARATOR) {

    val TYPE_ITEM_TITLE = 0
    val TYPE_ITEM = 1

    fun getUserWithCount(i : Int): User? = getItem(i)


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

    inner class ViewHolder(private val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(user: User?){
            with(binding as ItemUserBinding){
                Glide.with(itemView.context).load("user.link").placeholder(R.drawable.ic_person).into(binding.imgImg);
                binding.tvName.text = user?.displayName ?: binding.tvName.context.getString(R.string.nodata)
                binding.tvDesc.text = user?.university ?: binding.tvName.context.getString(R.string.nodata)

                binding.btnViewUser.setOnClickListener{
                    callBack.onItemUserClickListenner(user?.id!!)
                }
            }
        }
        fun onBindTitle(){
            with(binding as ItemTitleBinding){
               binding.tvTitle.text = binding.tvTitle.context.getString(R.string.menu_users)
               binding.tvMessage.text = binding.tvTitle.context.getString(R.string.total_user)
               binding.tvMessageIndex.text = "$itemCount"

               binding.cvSearch.setOnClickListener{
                   callBack.onItemSearchUserClickListenner()
               }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) return TYPE_ITEM_TITLE
        return TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == TYPE_ITEM_TITLE)
            return ViewHolder(ItemTitleBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        return ViewHolder(ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
}

//    override fun getItemCount(): Int {
//        return super.getItemCount() + 1
//    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder.itemViewType == TYPE_ITEM_TITLE) holder.onBindTitle()
        else if ((holder.itemViewType == TYPE_ITEM))
        holder.onBind(getItem(position - 1))
    }
}