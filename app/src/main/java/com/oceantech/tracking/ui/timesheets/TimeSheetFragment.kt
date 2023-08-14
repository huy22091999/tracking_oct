package com.oceantech.tracking.ui.timesheets

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.lifecycle.SavedStateHandle
import androidx.recyclerview.widget.RecyclerView
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryOwner
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
import com.oceantech.tracking.utils.EventDecorator
import com.oceantech.tracking.utils.checkError
import com.oceantech.tracking.utils.registerNetworkReceiver
import com.oceantech.tracking.utils.setupRecycleView
import com.oceantech.tracking.utils.showToast
import com.oceantech.tracking.utils.toDate
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import javax.inject.Inject
import kotlin.random.Random

@SuppressLint("LogNotTimber")
@AndroidEntryPoint
class TimeSheetFragment @Inject constructor() : TrackingBaseFragment<FragmentTimeSheetBinding>() {

    private val timeSheetViewModel: TimeSheetViewModel by activityViewModel()
    private lateinit var timeSheetAdapter: TimeSheetAdapter
    private lateinit var calendarView: MaterialCalendarView
    private var showList: Boolean = true
    private var events = mutableListOf<EventDecorator>()
    private var timeSheets = mutableListOf<TimeSheet>()

    companion object {
        private const val GET_ALL = 123
        private const val CHECK_IN = 234
        private const val SHOW_LIST = "show_list"
    }

    private var stateTimeSheet: Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerNetworkReceiver {
            timeSheetViewModel.handle(TimeSheetViewAction.AllTimeSheets)
            stateTimeSheet = GET_ALL
        }

    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTimeSheetBinding {
        return FragmentTimeSheetBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            showList = savedInstanceState.getBoolean(SHOW_LIST)
        }
        timeSheetAdapter = TimeSheetAdapter()
        setupRecycleView(views.timeSheetRV, timeSheetAdapter, requireContext())
        views.fabAddTimeSheet.setOnClickListener {
            timeSheetViewModel.handle(
                TimeSheetViewAction.CheckInAction(
                    ip = Random.nextInt().toString()
                )
            )
            stateTimeSheet = CHECK_IN
        }

        timeSheetViewModel.onEach {
            views.timeSheetPB.isVisible = it.isLoading() || it.getAllTimeSheetState is Fail
            views.timeSheetRV.isVisible =
                !it.isLoading() && it.getAllTimeSheetState is Success && showList
            views.fabAddTimeSheet.isVisible = !it.isLoading() && it.getAllTimeSheetState is Success
            views.listOrCalendar.isVisible = !it.isLoading() && it.getAllTimeSheetState is Success
            views.timeSheetCalendarLayout.isVisible =
                !it.isLoading() && it.getAllTimeSheetState is Success && !showList
        }
        calendarView = views.timeSheetCalendar

        calendarView.apply {
            setSelectedDate(Calendar.getInstance())
            setOnDateChangedListener { widget, date, selected ->
                if (selected) {
                    checkInSelectedDate(date)
                }
            }
            setTitleMonths(resources.getStringArray(R.array.months_calendar))
        }

        views.listOrCalendar.apply {
            setOnClickListener {
                showList = !showList
                setImageResource(if (showList) R.drawable.calendar else R.drawable.list)
                views.timeSheetRV.isVisible = showList
                views.timeSheetCalendarLayout.isVisible = !showList
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SHOW_LIST, showList)
    }

    override fun invalidate(): Unit = withState(timeSheetViewModel) {
        when (stateTimeSheet) {
            GET_ALL -> handleGetAll(it)
            CHECK_IN -> handleCheckIn(it)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun handleGetAll(state: TimeSheetViewState) {
        when (state.getAllTimeSheetState) {
            is Success -> {
                state.getAllTimeSheetState.invoke().let { timeSheets ->
                    this.timeSheets.addAll(timeSheets)
                    timeSheetAdapter.setListTimeSheet(timeSheets)
                    timeSheetAdapter.notifyDataSetChanged()
                    for (timeSheet in timeSheets) {
                        events.add(
                            EventDecorator(
                                CalendarDay.from(timeSheet.dateAttendance?.let { toDate(it) })
                            )
                        )
                    }
                    calendarView.addDecorators(events)
                    checkInSelectedDate(calendarView.selectedDate)
                }
            }

            is Fail -> {
                state.getAllTimeSheetState.error.message?.let { error ->
                    checkError(error)
                }
            }

            else -> {}
        }
    }

    private fun handleCheckIn(state: TimeSheetViewState) {
        when (state.checkInState) {
            is Success -> {
                showToast(requireContext(), getString(R.string.check_in_tracking_successfully))
                timeSheetViewModel.handle(TimeSheetViewAction.AllTimeSheets)
                stateTimeSheet = GET_ALL
            }

            is Fail -> {
                state.checkInState.error.message?.let { error ->
                    checkError(error)
                }

            }

            else -> {}
        }
    }

    private fun checkInSelectedDate(date: CalendarDay) {
        val checkedIn = timeSheets.filter {
            CalendarDay.from(it.dateAttendance?.let { it1 -> toDate(it1) }) == date
        }
        views.checkCheckIn.text =
            if (checkedIn.isNotEmpty()) getString(R.string.checked_in) else getString(R.string.not_check_in)
    }
}