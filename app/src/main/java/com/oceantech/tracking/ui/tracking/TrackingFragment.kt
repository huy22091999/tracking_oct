package com.oceantech.tracking.ui.tracking

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.databinding.FragmentTrackingBinding
import com.oceantech.tracking.ui.home.HomeViewAction
import com.oceantech.tracking.ui.home.HomeViewEvent
import com.oceantech.tracking.ui.home.HomeViewModel


class TrackingFragment : TrackingBaseFragment<FragmentTrackingBinding>() {
    private val viewModel:HomeViewModel by activityViewModel()
    lateinit var content:String

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTrackingBinding = FragmentTrackingBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.observeViewEvents {
            handleEvent(it)
        }
        views.btnSaveTracking.setOnClickListener {
            saveTracking()
        }
//        viewModel.handle(HomeViewAction.GetTrackings)
        super.onViewCreated(view, savedInstanceState)
    }

    private fun saveTracking() {
        content = views.tracking.text.toString().trim()
        if(content.isNullOrEmpty()){
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.channel_description)
                .setMessage(R.string.feedback_empty)
                .setNegativeButton(R.string.ok, null)
                .show()
        }
        else if(!content.isNullOrEmpty()){
            viewModel.handle(HomeViewAction.SaveTracking(content))
        }
    }

    private fun handleEvent(it:HomeViewEvent){
        when(it){
            is HomeViewEvent.ResetLanguege ->{
                views.title2.text = getString(R.string.tracking_description)
                views.tracking.hint = getString(R.string.tracking_hint)
                views.btnSaveTracking.text = getString(R.string.save_tracking)
            }
        }
    }

    override fun invalidate():Unit = withState(viewModel){
        when(it.asyncSaveTracking){
            is Success -> {
                Toast.makeText(requireActivity(), getString(R.string.tracking_success), Toast.LENGTH_SHORT).show()
            }
            is Fail -> {
                Log.e("Test Save Tracking: ", "Fail")
            }
        }
//        when(it.allTracking){
//            is Success -> {
//                it.allTracking.invoke().let { trackings ->
//                    for(i in trackings) Log.i("Trackings:", i.content.toString())
//                }
//            }
//            is Fail -> {
//                Log.e("Get Trackings: ", "Fail")
//            }
//        }
    }

}
