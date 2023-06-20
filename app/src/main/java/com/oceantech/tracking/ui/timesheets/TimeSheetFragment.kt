package com.oceantech.tracking.ui.timesheets

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
import javax.inject.Inject

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
        timeSheetRV.addItemDecoration(ItemDecoration(20))
        timeSheetRV.layoutManager = LinearLayoutManager(requireContext())
        timeSheetAdapter = TimeSheetAdapter()
        timeSheetRV.adapter  = timeSheetAdapter
        views.fabAddTimeSheet.setOnClickListener {
            checkInTimeSheet()
        }
    }

    private fun checkInTimeSheet() {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.add_new_timesheet, null)
        val edtTimeSheetId = view.findViewById<EditText>(R.id.edtTimeSheetID)
        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setTitle(getString(R.string.add_new_time_sheet))
            setIcon(R.drawable.time_sheet_add)
            setView(view)
            setPositiveButton(requireContext().getString(R.string.add_new_tracking)){dialog, which ->
                val id = edtTimeSheetId.text.toString()
                timeSheetViewModel.handle(TimeSheetViewAction.CheckInAction(id))
            }
            setNegativeButton(requireContext().getString(R.string.Cancel), null)
        }.create().show()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun invalidate(): Unit = withState(timeSheetViewModel){
        when(it.getAllTimeSheetState){
            is Loading -> {
                views.timeSheetPB.visibility = View.VISIBLE
                views.timeSheetRV.visibility = View.GONE
                views.fabAddTimeSheet.visibility = View.GONE
            }
            is Success ->{
                views.timeSheetPB.visibility = View.GONE
                views.timeSheetRV.visibility = View.VISIBLE
                views.fabAddTimeSheet.visibility = View.VISIBLE
                timeSheetAdapter.setListTimeSheet(it.getAllTimeSheetState.invoke()!!)
                timeSheetAdapter.notifyDataSetChanged()
            }
            is Fail -> {
                Log.i("TimeSheets", (it.getAllTimeSheetState as Fail<List<TimeSheet>>).error.toString())
            }
        }
        when(it.checkInState){
            is Success -> {
                Toast.makeText(requireContext(), getString(R.string.create_tracking_successfully), Toast.LENGTH_SHORT).show()
                it.checkInState = Uninitialized
                timeSheetViewModel.handle(TimeSheetViewAction.AllTimeSheets)
            }
            is Fail -> {
                Toast.makeText(requireContext(), getString(R.string.create_tracking_failed), Toast.LENGTH_SHORT).show()
                Log.i("TimeSheets", (it.checkInState as Fail<TimeSheet>).error.toString())
            }
        }
    }
}