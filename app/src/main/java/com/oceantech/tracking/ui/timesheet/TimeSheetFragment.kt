package com.oceantech.tracking.ui.timesheet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.oceantech.tracking.ui.home.HomeViewEvent
import com.oceantech.tracking.ui.home.HomeViewModel
import java.net.NetworkInterface
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Date

class TimeSheetFragment : TrackingBaseFragment<FragmentTimeSheetBinding>() {
    private val viewModel: HomeViewModel by activityViewModel()
    private lateinit var timeSheets:List<TimeSheet>
    private lateinit var listHasCheckIn:MutableList<LocalDate>
    private lateinit var calendaradapter:CalendarAdapter
    private lateinit var selectDate:LocalDate
    private lateinit var timeSheetAdapter: TimeSheetAdapter

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTimeSheetBinding = FragmentTimeSheetBinding.inflate(inflater,container,false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.handle(HomeViewAction.GetTimeSheets)
        timeSheets = listOf()
        listHasCheckIn = mutableListOf<LocalDate>()

        timeSheetAdapter = TimeSheetAdapter(timeSheets)

        selectDate = LocalDate.now()
        views.apply {
            preMonth.setOnClickListener {
                previousMonthAction()
                Log.i("Date now: ", selectDate.toString())
            }
            nextMonth.setOnClickListener {
                nextMonthAction()
                Log.i("Date now: ", selectDate.toString())
            }

            checkInSubmit.setOnClickListener {
                checkIn()
            }

            timeSheets.layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.observeViewEvents {
            handleEvent(it)
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setMonth(){
        val formatter:DateTimeFormatter = DateTimeFormatter.ofPattern("MMM yyyy")
        val monthYear = selectDate.format(formatter)

        views.monthLabel.text = monthYear
        val daysInMonth:Map<Int,Boolean> = daysInMonthList(selectDate, listHasCheckIn)

        calendaradapter = CalendarAdapter(daysInMonth, requireContext())
        views.calendar.apply {
            layoutManager = GridLayoutManager(requireContext(),7)
            adapter = calendaradapter
        }
    }

    private fun daysInMonthList(date: LocalDate, listHasCheckIn: List<LocalDate>): Map<Int, Boolean> {
        val days = mutableMapOf<Int, Boolean>()
        val yearMonth = YearMonth.from(date)
        val daysInMonth = yearMonth.lengthOfMonth()
        val firstOfMonth = date.withDayOfMonth(1)
        val daysInWeek = firstOfMonth.dayOfWeek.value

        for (i in 1..42) {
            if (i <= daysInWeek || i > daysInMonth + daysInWeek) {
                days[(-1)*i] = false
            } else {
                val currentDay = i - daysInWeek
                val dayOfMonth = date.withDayOfMonth(currentDay)
                val hasCheckIn = listHasCheckIn.any { it.month == dayOfMonth.month && it.dayOfMonth == dayOfMonth.dayOfMonth && it.year == date.year}
                days[currentDay] = hasCheckIn
            }
        }

        return days
    }

    private fun previousMonthAction() {
        selectDate = selectDate.minusMonths(1)
        setMonth()
    }

    private fun nextMonthAction() {
        selectDate = selectDate.plusMonths(1)
        setMonth()
    }

    private fun convertToDate(dateString: String): LocalDate {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        return LocalDate.parse(dateString, formatter)
    }
    private fun handleEvent(event: HomeViewEvent){
        when(event){
            is HomeViewEvent.ResetLanguege ->{

            }
        }
    }

    private fun checkIn(){
        viewModel.handle(HomeViewAction.GetCheckIn(getIPAddress()))
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

    override fun invalidate():Unit = withState(viewModel){
        when(it.timeSheets){
            is Success -> {
                it.timeSheets.invoke()?.let {
                    for(i in it){
                        listHasCheckIn.add(convertToDate(i.dateAttendance.toString()))
                    }
                    views.timeSheets.adapter = TimeSheetAdapter(it.reversed())
                    setMonth()
                }
                viewModel.handleRemoveStateOfCheckIn()
            }
            is Loading -> {
                Log.e("state of time sheets:", "loading")
            }
            is Fail -> {
                Log.e("state of time sheets:", "fail")
            }
        }
        when(it.checkIn){
            is Success -> {
                it.checkIn?.invoke().let { checkIn ->
                    if(checkIn.message.isNullOrEmpty()){
                        Toast.makeText(requireContext(), checkIn.message, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.checked_in), Toast.LENGTH_SHORT).show()
                    }
                }
                viewModel.handleRemoveStateOfCheckIn()
            }
            is Fail -> {
                Toast.makeText(requireContext(), getString(R.string.checked_in), Toast.LENGTH_SHORT).show()
            }
            is Loading -> {
                viewModel.handleTimeSheets()
            }
        }
    }
}