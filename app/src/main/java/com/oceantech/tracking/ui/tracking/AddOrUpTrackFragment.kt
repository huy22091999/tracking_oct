package com.oceantech.tracking.ui.tracking

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.databinding.FragmentAddOrUpTrackingBinding
import com.oceantech.tracking.databinding.FragmentTrackingBinding
import com.oceantech.tracking.ui.home.HomeViewAction
import com.oceantech.tracking.ui.home.HomeViewEvent
import com.oceantech.tracking.ui.home.HomeViewModel


class AddOrUpTrackFragment : TrackingBaseFragment<FragmentAddOrUpTrackingBinding>() {
    private val viewModel: HomeViewModel by activityViewModel()
    private var id:Int = -1
    private var content:String = ""
    private val arg:AddOrUpTrackFragmentArgs by navArgs()

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAddOrUpTrackingBinding = FragmentAddOrUpTrackingBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.observeViewEvents {
            handleEvent(it)
        }
        views.btnSaveTracking.setOnClickListener {
            if(id != -1){
                update()
            } else{
                saveTracking()
            }
        }
        id = arg.id
        if(id != -1){
            views.tracking.setText(arg.content)
        }
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

    private fun update(){
        content = views.tracking.text.toString().trim()
        viewModel.handle(HomeViewAction.UpdateTracking(id, content))
    }

//    private fun updateCountDayTracking(){
//        viewModel.handle(HomeViewAction.UpdateMyself())
//    }

    private fun handleEvent(it: HomeViewEvent){
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
                viewModel.handleReturnTracking()
                viewModel.handleRemoveStateOfAdd()
            }
            is Loading -> {
                viewModel.handleAllTracking()
            }
            is Fail -> {
                Log.e("Test Save Tracking: ", "Fail")
            }
            is Uninitialized -> {

            }
        }
        when(it.asyncUpdateTracking){
            is Success -> {
                Toast.makeText(requireActivity(), getString(R.string.tracking_success), Toast.LENGTH_SHORT).show()
                viewModel.handleReturnTracking()
                viewModel.handleRemoveStateOfUpdate()
            }
            is Loading -> {
                viewModel.handleAllTracking()
            }
            is Fail -> {
                Log.e("Test Save Tracking: ", "Fail")
            }
            is Uninitialized ->{

            }
        }
    }
}
