package com.oceantech.tracking.ui.timesheet

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.TimeSheet
import com.oceantech.tracking.databinding.FragmentTimeSheetBinding
import com.oceantech.tracking.utils.DialogUtil
import com.oceantech.tracking.utils.StringUltis
import com.oceantech.tracking.utils.checkStatusApiRes
import com.oceantech.tracking.utils.convertToCalendarDay
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.NetworkInterface.*
import java.util.Enumeration


class TimeSheetFragment : TrackingBaseFragment<FragmentTimeSheetBinding>() {

    val timeSheetViewModel: TimeSheetViewModel by activityViewModel()
    private val selectedDates: MutableList<CalendarDay> =
        mutableListOf()  // Sử dụng danh sách rỗng ban đầu
    private var timeSheets: MutableList<TimeSheet> = mutableListOf()
    private lateinit var decorator: SelectedDateDecorator
    private var offline: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTimeSheetBinding {
        return FragmentTimeSheetBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        state = GET_TIMESHEET
        timeSheetViewModel.handle(TimeSheetViewAction.getTimeSheetAction)
        listennerClickUI()

        decorator = SelectedDateDecorator(requireActivity(), selectedDates)
        views.calenderEvent.addDecorators(decorator)
        setupCalendar()
    }

    private fun setupCalendar() {
        views.calenderEvent.setOnDateChangedListener { widget, date, selected ->
            val selectedTimeSheet = timeSheets.find {
                it.dateAttendance!!.convertToCalendarDay(StringUltis.dateIso8601Format) == date
            }
            if (selectedTimeSheet != null) {
                // Hiển thị thông báo nếu có TimeSheet và có message
                val messageToDisplay = selectedTimeSheet?.message ?: "Không có thông tin chi tiết."

                val alertDialog = AlertDialog.Builder(widget.context)
                    .setTitle("Thông báo")
                    .setMessage(messageToDisplay)
                    .setPositiveButton("Đóng", null)
                    .create()
                alertDialog.show()
            }
        }
    }


    private fun listennerClickUI() {
        views.btnCheckin.setOnClickListener {
            state = CHECK_IN
            timeSheetViewModel.handle(TimeSheetViewAction.checkinAction(getLocalIpAddress()))
        }
    }

    override fun invalidate() = withState(timeSheetViewModel) {
        when (state) {
            GET_TIMESHEET -> handleGetTimeSheet(it)
            CHECK_IN -> handleCheckin(it)
        }
    }

    private fun handleCheckin(it: TimeSheetViewState) {
        when (it.checkin) {
            is Success -> {
                val currentDate = CalendarDay.today()
                val checkIn = it.checkin.invoke()
                timeSheets.add(checkIn)
                DialogUtil.showAlertDialogSuccess(requireActivity(), getString(R.string.checkInSuccess))
                if (checkIn.offline == true){
                    offline = true
                    decorator.addSelectedDate(offline!!, currentDate)
                    views.calenderEvent.invalidateDecorators()
                }
                else{
                    offline=false
                    decorator.addSelectedDate(offline!!, currentDate)
                    views.calenderEvent.invalidateDecorators()
                }

            }

            is Fail -> {
                DialogUtil.showAlertDialogAlert(requireActivity(), getString(R.string.checkInSuccess))
                Toast.makeText(
                    requireContext(),
                    getString(checkStatusApiRes(it.checkin)),
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                false
            }
        }
    }

    private fun handleGetTimeSheet(it: TimeSheetViewState) {
        when (it.timeSheets) {
            is Success -> {
                timeSheets.addAll(it.timeSheets.invoke())
                //timeSheets = it.timeSheets.invoke() as MutableList<TimeSheet>
                it.timeSheets.invoke().forEach {
                    if (it.offline == true) {
                        offline = true
                        it.dateAttendance!!.convertToCalendarDay(StringUltis.dateIso8601Format)
                            ?.let { calenderDay ->
                                decorator.addSelectedDate(
                                    offline!!,
                                    calenderDay
                                )
                            } ?: run {
                            // Nếu không thực hiện thành công, hiển thị cảnh báo Toast
                            Toast.makeText(
                                requireContext(),
                                "Thất bại trong quá trình chuyển đổi!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        offline = false
                        it.dateAttendance!!.convertToCalendarDay(StringUltis.dateIso8601Format)
                            ?.let { calenderDay ->
                                decorator.addSelectedDate(
                                    offline!!,
                                    calenderDay
                                )
                                views.calenderEvent.invalidateDecorators()
                            } ?: run {
                            // Nếu không thực hiện thành công, hiển thị cảnh báo Toast
                            Toast.makeText(
                                requireContext(),
                                "Thất bại trong quá trình chuyển đổi!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

            is Fail -> {
                Toast.makeText(
                    requireContext(),
                    getString(checkStatusApiRes(it.timeSheets)),
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> false
        }
    }

    fun getLocalIpAddress(): String {
        try {
            val interfaces = getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                val addresses = networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()
                    // Kiểm tra nếu là địa chỉ IP IPv4 và không phải địa chỉ loopback
                    if (!address.isLoopbackAddress && (":" in address.hostAddress).not()) {
                        return address.hostAddress
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    companion object {
        private const val DEFAULT_STATE = 0
        private const val GET_TIMESHEET = 1
        private const val CHECK_IN = 2
    }

    private var state = DEFAULT_STATE
}