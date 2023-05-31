package com.oceantech.tracking.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.activityViewModel
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.Category
import com.oceantech.tracking.databinding.FragmentListNewsBinding
import com.oceantech.tracking.ui.MainActivity
import com.oceantech.tracking.ui.home.HomeViewEvent
import com.oceantech.tracking.ui.home.HomeViewmodel
import com.oceantech.tracking.ui.medical.MedicalLoadStateAdapter
import kotlinx.coroutines.flow.collectLatest


class ListNewsFragment :TrackingBaseFragment<FragmentListNewsBinding>() {
    private lateinit var pagingAdapter: NewsAdapter
    private var category:Category? = null
    val viewModel:HomeViewmodel by activityViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        category=viewModel.getCategory()
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentListNewsBinding {
        return FragmentListNewsBinding.inflate(inflater,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeViewEvents {
            handleEvent(it)
        }

        views.back.setOnClickListener {
            findNavController().popBackStack()
        }
        populateData()
    }
    fun populateData()
    {
        views.titleCategory.text=category?.title
        pagingAdapter =NewsAdapter(requireContext()){_,item ->
            viewModel.setNew(item)
            (activity as MainActivity).navigateTo(R.id.action_listNewsFragment_to_detailNewsFragment)
        }
        views.recyclerViewNews.apply {
            layoutManager = LinearLayoutManager(requireContext())

        }
        views.recyclerViewNews.adapter = pagingAdapter.withLoadStateHeaderAndFooter(
            header = MedicalLoadStateAdapter { pagingAdapter.retry() },
            footer = MedicalLoadStateAdapter { pagingAdapter.retry() }
        )
        lifecycleScope.launchWhenCreated  {
            viewModel.getNews(viewModel.language,category!!).collectLatest { pagingData ->
                pagingAdapter.submitData(pagingData)
            }
        }
    }

    private fun handleEvent(it: HomeViewEvent) {
        when(it)
        {
            is HomeViewEvent.ResetLanguege->{
                populateData()
            }
        }
    }
}