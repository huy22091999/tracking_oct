package com.oceantech.tracking.ui.tracking

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.databinding.FragmentAllTrackingBinding
import com.oceantech.tracking.ui.home.HomeViewModel

class AllTrackingFragment : TrackingBaseFragment<FragmentAllTrackingBinding>() {
    private val viewModel:HomeViewModel by activityViewModel()

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAllTrackingBinding = FragmentAllTrackingBinding.inflate(inflater, container, false)

    override fun invalidate():Unit = withState(viewModel){
        when(it.allTracking){
            is Success -> {
                it.allTracking.invoke().let { trackings ->
                    views.recyclerView.apply {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(requireActivity())
                        adapter = TrackingAdapter(trackings)
                    }
                    Log.i("Trackings:", "${trackings.size}")
                }
                dismissLoadingDialog()
            }
            is Loading ->{
                showLoadingDialog()
            }
            is Fail -> {
                Log.e("Get Trackings: ", "Fail")
                dismissLoadingDialog()
            }
        }
    }
}