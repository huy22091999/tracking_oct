package com.oceantech.tracking.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.activityViewModel
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.databinding.FragmentHomeBinding
import com.oceantech.tracking.ui.MainActivity
import javax.inject.Inject

class HomeFragment @Inject constructor() : TrackingBaseFragment<FragmentHomeBinding>() {

    private val viewModel:HomeViewmodel by activityViewModel()

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        views.homeToCategory.setOnClickListener {
            (activity as MainActivity).navigateTo(R.id.action_FirstFragment_to_newsFragment)
        }
        viewModel.observeViewEvents {
            handleEvent(it)
        }

    }
    private fun handleEvent(it: HomeViewEvent) {
        when(it)
        {
            is HomeViewEvent.ResetLanguege->{
                views.title.text=getString(R.string.home_everyone)
                views.homeToCategory.text=getString(R.string.home_button)
            }
        }
    }

}