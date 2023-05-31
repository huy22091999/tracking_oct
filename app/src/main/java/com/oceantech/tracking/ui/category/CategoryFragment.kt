package com.oceantech.tracking.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.Category
import com.oceantech.tracking.databinding.FragmentNewsBinding
import com.oceantech.tracking.ui.MainActivity
import com.oceantech.tracking.ui.home.HomeViewAction
import com.oceantech.tracking.ui.home.HomeViewEvent
import com.oceantech.tracking.ui.home.HomeViewmodel

class CategoryFragment :TrackingBaseFragment<FragmentNewsBinding>() {
    val viewModel:HomeViewmodel by activityViewModel()
    private lateinit var adapter: CategoryAdapter
    private lateinit var list: List<Category>

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentNewsBinding {
        return FragmentNewsBinding.inflate(inflater,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.handle(HomeViewAction.GetCategorys)
        list= listOf()
        adapter= CategoryAdapter(requireContext(),list)
        views.gridLayout.adapter=adapter
        views.gridLayout.setOnItemClickListener { _, _, position, _ ->
            viewModel.setCategory(list[position])
            (activity as MainActivity).navigateTo(R.id.action_nav_newsFragment_to_listNewsFragment)
        }
        viewModel.observeViewEvents {
            handleEvent(it)
        }
    }
    fun handleEvent(event: HomeViewEvent)
    {
        when(event)
        {
            is HomeViewEvent.ResetLanguege->{
                views.title.text=getString(R.string.category_theme)
                viewModel.handle(HomeViewAction.GetCategorys)
            }
        }

    }

    override fun invalidate() = withState(viewModel){
        when(it.asyncCategory)
        {
            is Success->{
                it.asyncCategory.invoke().let {
                    list=it.content!!
                    adapter= CategoryAdapter(requireContext(),list)
                    views.gridLayout.adapter=adapter
                    //adapter.notifyDataSetChanged()
                }

            }
        }
    }

}