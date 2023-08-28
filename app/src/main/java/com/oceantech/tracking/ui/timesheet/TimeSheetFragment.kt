package com.oceantech.tracking.ui.timesheet

import android.R
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import com.airbnb.mvrx.Async.Companion.getMetadata
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.conn.util.InetAddressUtils
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.TimeSheet
import com.oceantech.tracking.databinding.DialogTimesheetBinding
import com.oceantech.tracking.databinding.FragmentTimeSheetBinding
import com.oceantech.tracking.ui.tracking.TrackingViewEvent
import com.oceantech.tracking.utils.*
import retrofit2.http.GET
import timber.log.Timber
import java.net.NetworkInterface
import java.util.*


class TimeSheetFragment : TrackingBaseFragment<FragmentTimeSheetBinding>() {

    companion object {
        private const val DEFAULT_STATE = 0
        private const val GET_TIMESHEET = 1
        private const val CHECK_IN = 2
    }

    private var state = DEFAULT_STATE


    val mViewModel: TimeSheetViewModel by activityViewModel()

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
    }

    private fun setupCalendar() {
        views.tvTime.text = Date().convertDateToStringFormat(StringUltis.dateMonthFormat)
        views.calendar.setUseThreeLetterAbbreviation(true)
        views.calendar.setEventIndicatorStyle(CompactCalendarView.NO_FILL_LARGE_INDICATOR)
        views.calendar.shouldDrawIndicatorsBelowSelectedDays(true)
        views.calendar.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
                withState(mViewModel) { it ->
                    it.timeSheets.invoke()!!.forEach {
                        if (dateClicked?.compareWithString(it.dateAttendance!!, StringUltis.dateIso8601Format) == true) {
                            mViewModel.handleReturnShowDetailTimeSheet(it)
                            return@forEach
                        }
                    }
                }
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                views.tvTime.text = firstDayOfNewMonth?.convertDateToStringFormat(StringUltis.dateMonthFormat)
            }
        })
    }

    private fun handlEvent(it: TimeSheetViewEvent) {
        when (it) {
          is TimeSheetViewEvent.ReturnDetailTimeSheetViewEvent -> handleShowDialogDetail(it.timeSheet)
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
                Toast.makeText(requireContext(), checkin.message, Toast.LENGTH_SHORT).show()
            }
            is Fail -> {
                Toast.makeText(requireContext(), getString(checkStatusApiRes(it.checkin)), Toast.LENGTH_SHORT).show()
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
        var dialog = Dialog(requireContext())
        var bindingDialog = DialogTimesheetBinding.inflate(dialog.layoutInflater)
        dialog.setContentView(bindingDialog.root)

        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setGravity(Gravity.BOTTOM);
        dialog.show()

        if (timeSheet.offline == true) bindingDialog.tvDate.setTextColor(Color.GREEN)
        else bindingDialog.tvDate.setTextColor(Color.RED)
        bindingDialog.tvDate.text = timeSheet.dateAttendance!!.convertToStringFormat(StringUltis.dateIso8601Format, StringUltis.dateDayTimeFormat)
        bindingDialog.tvMessage.text = timeSheet.message ?: getString(com.oceantech.tracking.R.string.no_message)
    }

}