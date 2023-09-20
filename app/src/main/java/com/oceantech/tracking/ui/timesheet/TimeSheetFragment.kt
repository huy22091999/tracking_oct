package com.oceantech.tracking.ui.timesheet

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.*

import android.widget.Toast
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.TimeSheet
import com.oceantech.tracking.databinding.FragmentTimeSheetBinding
import com.oceantech.tracking.utils.*
import com.oceantech.tracking.utils.StringUltis.dateIso8601Format
import java.net.NetworkInterface
import java.util.*


@SuppressLint("SetTextI18n")
class TimeSheetFragment : TrackingBaseFragment<FragmentTimeSheetBinding>() {

    companion object {
        private const val DEFAULT_STATE = 0
        private const val GET_TIMESHEET = 1
        private const val CHECK_IN = 2
    }

    private var state = DEFAULT_STATE


    val mViewModel: TimeSheetViewModel by activityViewModel()
    private var dateSelect: Date = Date()

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTimeSheetBinding {
        return FragmentTimeSheetBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        state = GET_TIMESHEET
        mViewModel.handle(TimeSheetViewAction.getTimeSheetAction)

        listennerClickUI()
        setupCalendar()
    }



    private fun listennerClickUI() {
        views.btnCheckin.setOnClickListener {
            mViewModel.handle(TimeSheetViewAction.checkinAction(getIPAddress()))
            state = CHECK_IN
        }

        mViewModel.observeViewEvents {
            handlEvent(it)
        }

        views.btnNext.setOnClickListener{
            views.calendar.scrollRight()
        }

        views.btnPrevius.setOnClickListener{
            views.calendar.scrollLeft()
        }
    }

    private fun setupCalendar() {
        views.tvTime.text = dateSelect.convertDateToStringFormat(StringUltis.dateMonthFormat)
        views.calendar.setUseThreeLetterAbbreviation(true)
        views.calendar.shouldDrawIndicatorsBelowSelectedDays(true)
        views.calendar.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
                withState(mViewModel) { it ->
                    it.timeSheets.invoke()!!.forEach {
                        if (dateClicked?.compareWithString(it.dateAttendance!!, StringUltis.dateIso8601Format) == true) {
                            handleShowDialogDetail(it)
                            return@forEach
                        }
                    }
                }
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                views.tvTime.text = firstDayOfNewMonth?.convertDateToStringFormat(StringUltis.dateMonthFormat)
                handleProgressBar(firstDayOfNewMonth)
            }
        })
    }


    private fun handleProgressBar(dateCurentCalendar: Date? ) {
        if (dateCurentCalendar != null) dateSelect = dateCurentCalendar
        val calendar: Calendar = Calendar.getInstance()
        calendar.time = dateSelect
        var totalDayCheckIn = 0

        withState(mViewModel){
            it.timeSheets.invoke().let { it ->
                it?.forEach {
                    var calendarTemp = Calendar.getInstance()
                    calendarTemp.time = it.dateAttendance?.convertToDateFormat(dateIso8601Format)!!

                    if (calendar.get(Calendar.MONTH) == calendarTemp.get(Calendar.MONTH)){
                        totalDayCheckIn++
                    }
                }
            }
            views.progress.progress = totalDayCheckIn
            views.progress.max = 30
            views.tvProgresss.text = getString(R.string.totalDay) + " $totalDayCheckIn/${calendar.getActualMaximum(Calendar.DAY_OF_MONTH)}"
        }
    }


    private fun handlEvent(it: TimeSheetViewEvent) {
        when (it) {
        }
    }

    override fun invalidate(): Unit = withState(mViewModel) {
        when (state) {
            GET_TIMESHEET -> handleGetTimeSheet(it)
            CHECK_IN -> handleCheckin(it)
        }
    }

    private fun handleGetTimeSheet(it: TimeSheetViewState) {
        when (it.timeSheets) {
            is Success -> {
                handleProgressBar(null)
                views.calendar.removeAllEvents()
                it.timeSheets.invoke().forEach {
                    var event: Event? = null
                    if (it.offline!!) event = Event(
                        Color.GREEN,
                        it.dateAttendance!!.convertToMillisFormat(StringUltis.dateIso8601Format),
                        ""
                    )
                    else event = Event(
                        Color.RED,
                        it.dateAttendance!!.convertToMillisFormat(StringUltis.dateIso8601Format),
                        ""
                    )
                    views.calendar.addEvent(event)
                }
            }
            is Fail -> {
                Toast.makeText(requireContext(), getString(checkStatusApiRes(it.timeSheets)), Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }


    private fun handleCheckin(it: TimeSheetViewState) {
        when (it.checkin) {
            is Success -> {
                val checkin = it.checkin.invoke()
                var event: Event? = null
                if (checkin.offline == true) event = Event(
                    Color.GREEN,
                    checkin.dateAttendance!!.convertToMillisFormat(StringUltis.dateIso8601Format),
                    ""
                )
                else event = Event(
                    Color.RED,
                    checkin.dateAttendance!!.convertToMillisFormat(StringUltis.dateIso8601Format),
                    ""
                )

                views.calendar.addEvent(event)
                showSnackbar(views.root, getString(R.string.success), null, R.color.text_title1){
                }
            }
            is Fail -> {
                showSnackbar(views.root, getString(R.string.failed), null, R.color.red){}
            }
            else -> {}
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


    private fun handleShowDialogDetail(timeSheet: TimeSheet) {
        TimeSheetDialogFragment.getInstance(timeSheet).show(requireActivity().supportFragmentManager, tag)
    }

}