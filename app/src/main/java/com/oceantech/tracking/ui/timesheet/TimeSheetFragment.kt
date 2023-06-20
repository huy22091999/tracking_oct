package com.oceantech.tracking.ui.timesheet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.TimeSheet
import com.oceantech.tracking.databinding.FragmentTimeSheetBinding
import com.oceantech.tracking.ui.home.HomeViewAction
import com.oceantech.tracking.ui.home.HomeViewModel

class TimeSheetFragment : TrackingBaseFragment<FragmentTimeSheetBinding>() {
    private val viewModel:HomeViewModel by activityViewModel()
    private lateinit var timeSheets:List<TimeSheet>
    private lateinit var adapter: TimeSheetAdapter
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTimeSheetBinding = FragmentTimeSheetBinding.inflate(inflater,container,false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        timeSheets = listOf()

        views.gridView.apply {
            layoutManager = StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL)
            adapter = TimeSheetAdapter(timeSheets)
        }
        viewModel.timeSheets.observe(viewLifecycleOwner){
            views.gridView.apply {
                timeSheets = it
                adapter = TimeSheetAdapter(timeSheets)
                adapter = adapter
            }
        }
        viewModel.handleTimeSheets()

        views.checkInSubmit.setOnClickListener {
            checkIn()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun checkIn(){
        val ip:String = views.checkInInput.text.toString()
        viewModel.handle(HomeViewAction.GetCheckIn(ip))
    }

    override fun invalidate():Unit = withState(viewModel){
        when(it.timeSheets){
            is Success -> {
                it.timeSheets.invoke().let { timeSheet ->
//                    timeSheets = timeSheet
//                    adapter = TimeSheetAdapter(timeSheets)
//                    views.gridView.adapter = adapter
                    Log.i("Time sheet list's size:",timeSheet.size.toString())
                }
                dismissLoadingDialog()
            }
            is Loading -> {
                Log.e("state of time sheets:", "loading")
                showLoadingDialog()
            }
            is Fail -> {
                Log.e("state of time sheets:", "fail")
                dismissLoadingDialog()
            }
        }
        when(it.checkIn){
            is Success -> {
                dismissLoadingDialog()
                Toast.makeText(requireContext(), getString(R.string.tracking_success), Toast.LENGTH_SHORT).show()
            }
            is Fail -> {
                dismissLoadingDialog()
            }
            is Loading -> {
                viewModel.handleTimeSheets()
                showLoadingDialog()
            }
        }
    }
}