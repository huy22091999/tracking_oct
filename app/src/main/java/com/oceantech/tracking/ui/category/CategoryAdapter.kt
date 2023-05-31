package com.oceantech.tracking.ui.category

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.oceantech.tracking.R
import com.oceantech.tracking.data.model.Category
import com.oceantech.tracking.ui.MainActivity

class CategoryAdapter(val context: Context, val list: List<Category>) : BaseAdapter() {
    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return 0L
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.category_list_item, parent, false)
        view.findViewById<TextView>(R.id.lable_category).text = list[position].title
        val image = view.findViewById<AppCompatImageView>(R.id.image_category)
//        val token = SessionManager(context).fetchAuthToken()
        var glideUrl = GlideUrl(
            MainActivity.linkImage.plus(list[position].titleImageUrl),
            LazyHeaders.Builder()
//                .addHeader("Authorization", "Bearer $token")
                .build()
        )
        Glide
            .with(context)
            .load(glideUrl)
            .optionalFitCenter()
            .into(image)
        return view
    }

}