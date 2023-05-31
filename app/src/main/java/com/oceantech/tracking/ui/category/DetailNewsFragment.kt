package com.oceantech.tracking.ui.category

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.activityViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.News
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.databinding.FragmentDetailNewsBinding
import com.oceantech.tracking.ui.MainActivity.Companion.linkImage
import com.oceantech.tracking.ui.home.HomeViewEvent
import com.oceantech.tracking.ui.home.HomeViewmodel
import java.text.SimpleDateFormat


class DetailNewsFragment : TrackingBaseFragment<FragmentDetailNewsBinding>() {
    private val viewModel: HomeViewmodel by activityViewModel()
    private lateinit var news: News

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        news = viewModel.getNew()
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDetailNewsBinding {
        return FragmentDetailNewsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        views.back.setOnClickListener {
            findNavController().popBackStack()
        }
        viewModel.observeViewEvents {
            handleEvent(it)
        }
        views.titleNew.text = news.title
        val token = SessionManager(requireContext()).fetchAuthToken()
        var glideUrl = GlideUrl(
            linkImage.plus(news.titleImageUrl),
            LazyHeaders.Builder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        )
            Glide
                .with(requireContext())
                .load(glideUrl)
                .optionalFitCenter()
                .into(
                    views.image
                )

        val encodedHtml = Base64.encodeToString(news.content!!.toByteArray(), Base64.NO_PADDING)
        views.content.loadData(encodedHtml, "text/html", "base64")
        views.author.text=news.realAuthor
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        views.timeCreate.text=if(news.publishDate!=null) sdf.format(news.publishDate) else "-"
        views.viewCount.text=String.format(getString(R.string.view_count),news.view)
    }

    private fun handleEvent(it: HomeViewEvent) {
        when(it)
        {
            is HomeViewEvent.ResetLanguege->{
                views.viewCount.text=String.format(getString(R.string.view_count),news.view)
                views.byauthor.text=getString(R.string.by_outhor)
                views.titleContent.text=getString(R.string.content_new)
            }
        }
    }

    private fun getSpannedText(text: String?): Spanned? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(text)
        }

    }
}