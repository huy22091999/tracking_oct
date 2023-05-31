package com.oceantech.tracking.ui.category

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.oceantech.tracking.data.model.News
import com.oceantech.tracking.databinding.ItemNewLayoutBinding
import com.oceantech.tracking.ui.MainActivity
import java.text.SimpleDateFormat

class NewsAdapter(
    val context: Context,
    private val onListItemClicked: (Int, News) -> Unit
) :
    PagingDataAdapter<News, NewsAdapter.NewsViewHolder>(COMPARATOR) {

    class NewsViewHolder(
        val context: Context,
        val binding: ViewBinding,
        private val onViewClicked: (Int, News) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindHeath(news: News) {
            with(binding as ItemNewLayoutBinding) {
//                val token = SessionManager(context).fetchAuthToken()
                var glideUrl = GlideUrl(
                    MainActivity.linkImage.plus(news.titleImageUrl),
                    LazyHeaders.Builder()
//                        .addHeader("Authorization", "Bearer $token")
                        .build()
                )
                Glide
                    .with(context)
                    .load(glideUrl)
                    .optionalFitCenter()
                    .into(image)

                title.text = news.title
                val sdf = SimpleDateFormat("hh:mm dd/MM/yyyy")
                val currentDate =
                    if (news.publishDate != null) sdf.format(news.publishDate) else "-"
                time.text = currentDate
                itemView.setOnClickListener {
                    onViewClicked(absoluteAdapterPosition,news)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bindHeath(it)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewsViewHolder {
        val itemBinding: ViewBinding =
            ItemNewLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return NewsViewHolder(context, itemBinding,onListItemClicked)
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<News>() {
            override fun areItemsTheSame(oldItem: News, newItem: News): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: News, newItem: News): Boolean =
                oldItem == newItem

        }
    }
}