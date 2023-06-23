package com.oceantech.tracking.ui.timesheets

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.TimeSheet
import com.oceantech.tracking.databinding.FragmentTimeSheetBinding
import com.oceantech.tracking.ui.timesheets.adapter.TimeSheetAdapter
import com.oceantech.tracking.ui.item_decoration.ItemDecoration
import com.oceantech.tracking.utils.setupRecycleView
import com.oceantech.tracking.utils.showToast
import javax.inject.Inject
import kotlin.random.Random

@SuppressLint("LogNotTimber")
class TimeSheetFragment @Inject constructor() : TrackingBaseFragment<FragmentTimeSheetBinding>() {

    private val timeSheetViewModel: TimeSheetViewModel by activityViewModel()
    private lateinit var timeSheetAdapter: TimeSheetAdapter
    private lateinit var timeSheetRV: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timeSheetViewModel.handle(TimeSheetViewAction.AllTimeSheets)
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTimeSheetBinding {
        return FragmentTimeSheetBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        timeSheetRV = views.timeSheetRV
        timeSheetAdapter = TimeSheetAdapter()
        setupRecycleView(timeSheetRV, timeSheetAdapter, requireContext())
        views.fabAddTimeSheet.setOnClickListener {
            timeSheetViewModel.handle(TimeSheetViewAction.CheckInAction(ip = Random.nextInt().toString()))
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun invalidate(): Unit = withState(timeSheetViewModel) {
        when (it.getAllTimeSheetState) {
            is Loading -> {
                views.timeSheetPB.visibility = View.VISIBLE
                views.timeSheetRV.visibility = View.GONE
                views.fabAddTimeSheet.visibility = View.GONE
            }
            is Success -> {
                views.timeSheetPB.visibility = View.GONE
                views.timeSheetRV.visibility = View.VISIBLE
                views.fabAddTimeSheet.visibility = View.VISIBLE
                it.getAllTimeSheetState.invoke()?.let {timeSheets ->
                    timeSheetAdapter.setListTimeSheet(timeSheets)
                    timeSheetAdapter.notifyDataSetChanged()
                }
                it.getAllTimeSheetState = Uninitialized
            }

            is Fail -> {
                Log.i(
                    "TimeSheets",
                    (it.getAllTimeSheetState as Fail<List<TimeSheet>>).error.toString()
                )
            }
        }
        when (it.checkInState) {
            is Success -> {
                showToast(requireContext(), getString(R.string.check_in_tracking_successfully))
                it.checkInState = Uninitialized
                timeSheetViewModel.handle(TimeSheetViewAction.AllTimeSheets)
            }

            is Fail -> {
                showToast(requireContext(), getString(R.string.check_in_tracking_failed))
                Log.i("TimeSheets", (it.checkInState as Fail<TimeSheet>).error.toString())
            }
        }
    }
}