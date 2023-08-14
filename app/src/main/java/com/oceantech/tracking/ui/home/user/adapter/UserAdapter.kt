package com.oceantech.tracking.ui.home.user.adapter

import android.content.ClipData
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.oceantech.tracking.R
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.ItemLoadingStateBinding
import com.oceantech.tracking.databinding.UserItemBinding
import com.oceantech.tracking.utils.TrackingBaseAdapter
import com.oceantech.tracking.utils.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class UserAdapter(
    private val showUserInformation: (User) -> Unit
) : PagingDataAdapter<User, UserAdapter.ViewHolder>(ItemComparator) {

    companion object {
        private const val LOADING_STATE = 1
        private const val USER_STATE = 2
    }

    inner class ViewHolder(private val _binding: ViewBinding) :
        RecyclerView.ViewHolder(_binding.root) {
        val binding: ViewBinding
            get() = _binding
    }

    private var authority: String = ""

    //    var list = emptyList<User>()
    fun setAuthority(authority: String) {
        this.authority = authority
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < itemCount - 1) {
            USER_STATE
        } else LOADING_STATE
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            USER_STATE -> {
                val user = getItem(position)
                val binding = holder.binding as UserItemBinding
                binding.user = user

                val year = user?.year ?: 0
                binding.txtYear.text = binding.root.context.let { context ->
                    context.getString(
                        R.string.user_student_year,
                        if (year >= 5) context.getString(R.string.graduated) else year.toString()
                    )
                }

                if (authority == "ROLE_ADMIN") {
                    binding.root.apply {
                        setOnClickListener {
                            if (user != null) {
                                showUserInformation(user)
                            }
                        }
                    }
                }
            }

            LOADING_STATE -> {
                val binding = holder.binding as ItemLoadingStateBinding
                CoroutineScope(Dispatchers.Main).launch {
                    delay(5000L)
                    binding.progressBar.visibility = View.GONE
                    binding.noUsers.visibility = View.VISIBLE
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = when (viewType) {
            LOADING_STATE -> ItemLoadingStateBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

            USER_STATE -> UserItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

            else -> throw Exception("Invalid viewType")
        }
        return ViewHolder(
            binding
        )
    }

    object ItemComparator : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }


}