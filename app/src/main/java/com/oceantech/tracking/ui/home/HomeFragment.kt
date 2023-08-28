package com.oceantech.tracking.ui.home

import android.graphics.Color
import android.os.Build
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
import com.github.sundeepk.compactcalendarview.CompactCalendarView.CompactCalendarViewListener
import com.github.sundeepk.compactcalendarview.CompactCalendarView.GONE
import com.github.sundeepk.compactcalendarview.CompactCalendarView.VISIBLE
import com.github.sundeepk.compactcalendarview.domain.Event
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.TimeSheet
import com.oceantech.tracking.data.network.UserApi
import com.oceantech.tracking.databinding.FragmentHomeBinding
import java.net.NetworkInterface
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Random
import javax.inject.Inject


class HomeFragment @Inject constructor(val api: UserApi) :
    TrackingBaseFragment<FragmentHomeBinding>() {

    private val viewModel: HomeViewModel by activityViewModel()
    var trackedDays: MutableList<String>? = null
    var timeSheets: List<TimeSheet>? = null
    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.handle(HomeViewAction.GetAllTimeSheet)
        viewModel.observeViewEvents {
            handleEvent(it)
        }
        setupUi()
        views.btnSave.setOnClickListener {
            viewModel.handle(HomeViewAction.CheckIn(getIPAddress()))
        }
        views.btnCalendarBack.setOnClickListener {
            views.compactcalendarView.scrollLeft()
            onCalenderListener()
        }
        views.btnCalendarNext.setOnClickListener {
            views.compactcalendarView.scrollRight()
            onCalenderListener()
        }
    }

    private fun onCalenderListener() {
        val calendar = Calendar.getInstance()
        calendar.time = views.compactcalendarView.firstDayOfCurrentMonth
        val monthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.time)
        views.month.text = monthName
    }


    private fun setupUi() {
        val dateFormat = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        } else {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        }
        views.compactcalendarView.setFirstDayOfWeek(Calendar.MONDAY)
        views.compactcalendarView.shouldDrawIndicatorsBelowSelectedDays(true)
        views.compactcalendarView.setListener(object : CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date) {
                views.trackingDay.text = ""
                views.checkinStatus.visibility = GONE
                var currentTimeSheet: TimeSheet? = null
                for (timeSheet in timeSheets!!) {
                    if (dateClicked.time == (dateFormat.parse(timeSheet.dateAttendance))?.time) {
                        currentTimeSheet = timeSheet
                        views.checkinStatus.visibility = VISIBLE
                        currentTimeSheet.let {
                            views.trackingDay.text = it.dateAttendance
                        }
                        break
                    }
                }
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date) {
                onCalenderListener()
            }
        })

    }



    private fun handleEvent(it: HomeViewEvent) {
        when (it) {
            is HomeViewEvent.ResetLanguege -> {
                views.calendarLable.text = getString(R.string.calendar_lable)
            }
        }
    }

    override fun invalidate(): Unit = withState(viewModel) {
        // Inside the invalidate() function
        when (it.timeSheets) {
            is Success -> {
                timeSheets = it.timeSheets.invoke()
                val trackedDays: MutableList<Long> = mutableListOf()
                val dateFormat = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
                } else {
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
                }

                for (timeSheet in timeSheets!!) {
                    try {
                        val date = dateFormat.parse(timeSheet.dateAttendance)
                        date?.let {
                            trackedDays.add(it.time)
                        }
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                }
                setTrackedDates(trackedDays) // Set the color of tracked dates
            }
        }

        when (it.timeSheet) {
            is Success -> {
                Toast.makeText(requireContext(), it.timeSheet.invoke().message, Toast.LENGTH_SHORT)
                    .show()
                viewModel.removeTimeSheet()
                viewModel.handle(HomeViewAction.GetAllTimeSheet)
            }

            is Fail -> {
                Toast.makeText(requireContext(), "You checked in", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun getIPAddress(): String {
        val interfaces = NetworkInterface.getNetworkInterfaces()
        while (interfaces.hasMoreElements()) {
            val networkInterface = interfaces.nextElement()
            val addresses = networkInterface.inetAddresses
            while (addresses.hasMoreElements()) {
                val address = addresses.nextElement()
                if (!address.isLoopbackAddress && address.hostAddress.contains(":").not()) {
                    return address.hostAddress
                }
            }
        }
        return ""
    }

    private fun setTrackedDates(trackedDays: List<Long>) {
        val calendarView = views.compactcalendarView
        val greenColor = Color.parseColor("#388E3C")
        val dateFormat = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        } else {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
        }
        calendarView.removeAllEvents()
        for (timestamp in trackedDays) {
            Log.d("timesheet", "setTrackedDates: " + dateFormat.format(timestamp))
            val event = Event(greenColor, timestamp)
            Log.d("timesheet", "setTrackedDates: " + event.toString())
            calendarView.addEvent(event)
        }
    }

}

