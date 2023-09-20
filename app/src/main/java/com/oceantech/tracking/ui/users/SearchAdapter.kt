package com.oceantech.tracking.ui.users

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingClickItem
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.ItemTitleBinding
import com.oceantech.tracking.databinding.ItemUserBinding
@SuppressLint("NotifyDataSetChanged")
class SearchAdapter(
    private val callBack: TrackingClickItem
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    var list: ArrayList<User> = ArrayList()

    fun addUser(list: ArrayList<User>){
        this.list = list
        notifyDataSetChanged()
    }

    fun removeUsers(){
        this.list.clear()
        notifyDataSetChanged()
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(list[position])
    }
}