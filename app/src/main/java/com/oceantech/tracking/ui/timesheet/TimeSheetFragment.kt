package com.oceantech.tracking.ui.timesheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.TimeSheet
import com.oceantech.tracking.databinding.FragmentTimeSheetBinding
import com.oceantech.tracking.ui.home.HomeViewModel

class TimeSheetFragment : TrackingBaseFragment<FragmentTimeSheetBinding>() {
    private val viewModel:HomeViewModel by activityViewModel()
    private lateinit var timeSheet:List<TimeSheet>
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTimeSheetBinding = FragmentTimeSheetBinding.inflate(inflater,container,false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        timeSheet = listOf()

        viewModel.timeSheets.observe(viewLifecycleOwner){
            timeSheet = it
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun invalidate():Unit = withState(viewModel){
        when(it.timeSheets){
            is Success -> {
                dismissLoadingDialog()
            }
            is Loading -> {
                showLoadingDialog()
            }
            is Fail -> {
                dismissLoadingDialog()
            }
        }
    }
}