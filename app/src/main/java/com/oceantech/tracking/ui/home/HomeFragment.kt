package com.oceantech.tracking.ui.home

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.github.sundeepk.compactcalendarview.CompactCalendarView.CompactCalendarViewListener
import com.github.sundeepk.compactcalendarview.domain.Event
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.Notify
import com.oceantech.tracking.data.model.TimeSheet
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.network.UserApi
import com.oceantech.tracking.databinding.FragmentHomeBinding
import com.oceantech.tracking.utils.NotificationDialogFragment.Companion.FALURE_ID
import com.oceantech.tracking.utils.NotificationDialogFragment.Companion.SUCCESS_ID
import com.oceantech.tracking.utils.StringUltis.dateIso8601Format
import com.oceantech.tracking.utils.compareWithString
import com.oceantech.tracking.utils.convertToMillisFormat
import com.oceantech.tracking.utils.showDialog
import lecho.lib.hellocharts.model.PieChartData
import lecho.lib.hellocharts.model.SliceValue
import lecho.lib.hellocharts.util.ChartUtils
import java.net.NetworkInterface
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
//done
class HomeFragment @Inject constructor(val api: UserApi) :
    TrackingBaseFragment<FragmentHomeBinding>() {
    private val viewModel: HomeViewModel by activityViewModel()

    //data
    private var mUser: User? = null
    private var timeSheets: List<TimeSheet>? = null
    private var mNotify: Notify? = null
    private var month: Int? = null
    private var year: Int? = null

    //dateForPieChart
    private var checkedInDays:Float = 0.0f
    private var uncheckedDays:Float = 0.0f
    private var notYetCheckedInDays:Float = 0.0f
    private var currentDate = Calendar.getInstance()

    companion object{
        private const val STATISTICS_FOR_MONTH = 0
        private const val STATISTICS_FOR_SIX_MONTHS = 1
        private const val STATISTICS_FOR_YEAR = 2
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        listenEvent()
    }

    private fun setupUi() {
        views.displayNameLable.text = "${mUser?.displayName}"
        views.levelLable.text = getString(R.string.year) + " ${mUser?.year}"
        onCalendarListener()
        views.compactcalendarView.setFirstDayOfWeek(Calendar.MONDAY)
        views.compactcalendarView.shouldDrawIndicatorsBelowSelectedDays(true)
        views.compactcalendarView.setListener(object : CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date) {
                for (timeSheet in timeSheets!!) {
                    if (dateClicked.compareWithString(
                            timeSheet.dateAttendance,
                            dateIso8601Format
                        )
                    ) {
                        timeSheet.let {
                            val action =
                                HomeFragmentDirections.actionNavHomeFragmentToHomeBottomsheetFragment(
                                    it.dateAttendance
                                )
                            findNavController().navigate(action)
                        }
                        break
                    }
                }
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date) {
                onCalendarListener()
            }
        })
    }

    private fun listenEvent() {
        views.btnSave.setOnClickListener {
            viewModel.handle(HomeViewAction.CheckIn(getIPAddress()))
        }
        views.btnCalendarBack.setOnClickListener {
            views.compactcalendarView.scrollLeft()
            onCalendarListener()
        }
        views.btnCalendarNext.setOnClickListener {
            views.compactcalendarView.scrollRight()
            onCalendarListener()
        }

    }

    private fun onCalendarListener() {
        val calendar = Calendar.getInstance()
        calendar.time = views.compactcalendarView.firstDayOfCurrentMonth
        month = calendar.get(Calendar.MONTH) + 1
        year = calendar.get(Calendar.YEAR)
        val formattedDate = "$month/$year"
        views.month.text = formattedDate
    }

    //---------------------handle Data For PieChart------------------------------------------------//

    private fun handleDataForPieChart(trackedDays: List<Long>,i:Int){
        checkedInDays = trackedDays.size.toFloat()
        when (i){
            STATISTICS_FOR_MONTH -> {
                handleDataInMonth()
            }
            STATISTICS_FOR_SIX_MONTHS -> {
                handleDataInSixMonths()
            }
            STATISTICS_FOR_YEAR -> {
                handleDataInYear()
            }
        }
    }

    private fun handleDataInYear() {
        notYetCheckedInDays=0.0f
        val oneYearAgo = currentDate.clone() as Calendar
        oneYearAgo.add(Calendar.YEAR, -1)
        val daysInOneYear = oneYearAgo.getActualMaximum(Calendar.DAY_OF_YEAR)
        uncheckedDays = daysInOneYear - checkedInDays
        drawPieChart(checkedInDays,uncheckedDays, notYetCheckedInDays)
    }

    private fun handleDataInSixMonths() {
        notYetCheckedInDays=0.0f
        val sixMonthsAgo = currentDate.clone() as Calendar
        sixMonthsAgo.add(Calendar.MONTH, -6)
        var totalDaysInSixMonths = 0
        while (sixMonthsAgo.before(currentDate)) {
            val daysInMonth = sixMonthsAgo.getActualMaximum(Calendar.DAY_OF_MONTH)
            totalDaysInSixMonths += daysInMonth
            sixMonthsAgo.add(Calendar.MONTH, 1)
        }
        uncheckedDays = totalDaysInSixMonths - checkedInDays
        drawPieChart(checkedInDays,uncheckedDays, notYetCheckedInDays)
    }

    private fun handleDataInMonth() {
        val totalDaysInMonth = currentDate.getActualMaximum(Calendar.DAY_OF_MONTH)
        val currentDay = currentDate.get(Calendar.DAY_OF_MONTH)
        uncheckedDays = currentDay - checkedInDays
        notYetCheckedInDays = (totalDaysInMonth - currentDay).toFloat()
        drawPieChart(checkedInDays,uncheckedDays,notYetCheckedInDays)
    }

    //---------------------End of handle data For pieChart----------------------------------------//

    //---------------------Draw pie chart---------------------------------------------------------//
    private fun drawPieChart(checkedInDays: Float,uncheckedDays: Float,notYetCheckedInDays: Float) {
        val pieChart = views.contentStatistical.pieChart
        val slices = ArrayList<SliceValue>()
        val totalDays = checkedInDays + uncheckedDays + notYetCheckedInDays

        val checkedInSlice = SliceValue(checkedInDays, ChartUtils.COLOR_GREEN)
        val uncheckedSlice = SliceValue(uncheckedDays, ChartUtils.COLOR_RED)
        val notYetCheckedInSlice = SliceValue(notYetCheckedInDays,ChartUtils.COLOR_BLUE)

        slices.apply {
            add(checkedInSlice)
            add(uncheckedSlice)
            add(notYetCheckedInSlice)
        }
        val pieData = PieChartData(slices)
        pieData.apply {
            setHasLabels(true).valueLabelTextSize = 12
            setHasCenterCircle(true).setCenterText1(getString(R.string.user_checkin_lable))
                .setCenterText1FontSize(16).centerText1Color = Color.parseColor("#0097A7")
            setHasCenterCircle(true).setCenterText2(getString(R.string.percent_text))
                .setCenterText2FontSize(12).centerText2Color =
                Color.parseColor("#0097A7")
        }
        pieChart.apply {
            pieChartData = pieData
            isChartRotationEnabled = false
            isValueSelectionEnabled = true
            visibility = View.VISIBLE
            startDataAnimation()
            setupLayoutStatistical(
                checkedInDays.toInt(),
                uncheckedDays.toInt(),
                notYetCheckedInDays.toInt(),
                totalDays.toInt()
            )
        }
    }

    private fun setupLayoutStatistical(
        checkedInDays: Int,
        uncheckedDays: Int,
        notYetCheckedInDays: Int,
        totalDaysInMonth: Int
    ) {
        views.contentStatistical.let {
            it.percentCheckin.text = getString(R.string.label_checkin) + " (${
                convertToPercentage(
                    checkedInDays,
                    totalDaysInMonth,
                    0
                )
            })"
            it.dayQuanlityCheckin.text = "${checkedInDays} " + getString(R.string.lable_day)
            it.percentNocheckin.text = getString(R.string.label_nocheckin) + " (${
                convertToPercentage(
                    uncheckedDays,
                    totalDaysInMonth,
                    0
                )
            })"
            it.dayQuanlityNocheckin.text = "${uncheckedDays} " + getString(R.string.lable_day)
            it.percentNotyet.text = getString(R.string.label_notyet) + " (${
                convertToPercentage(
                    notYetCheckedInDays,
                    totalDaysInMonth,
                    0
                )
            })"
            it.dayQuanlityNotyet.text = "${notYetCheckedInDays} " + getString(R.string.lable_day)
        }
    }

    private fun convertToPercentage(value: Int, total: Int, decimalPlaces: Int): String {
        val percentage = (value / total.toFloat()) * 100
        return String.format("%.${decimalPlaces}f%%", percentage)
    }

    //-------------------------End of draw pie chart----------------------------------------------//

    //-------------------------Invalidate---------------------------------------------------------//
    override fun invalidate(): Unit = withState(viewModel) {
            handleCurrentUser(it)
            handleTimeSheets(it)
            handleTimeSheet(it)
    }

    private fun handleTimeSheet(it: HomeViewState) {
        when (it.timeSheet) {
            is Success -> {
                mNotify = Notify(SUCCESS_ID, it.timeSheet.invoke().message)
                showDialog(mNotify!!, childFragmentManager)
                viewModel.handle(HomeViewAction.GetAllTimeSheet)
                viewModel.removeTimeSheet()
            }

            is Fail -> {
                mNotify = Notify(FALURE_ID, getString(R.string.checkin_unsuccessfully))
                showDialog(mNotify!!, childFragmentManager)
                viewModel.removeTimeSheet()
            }
        }
    }

    private fun handleTimeSheets(it: HomeViewState) {
        when (it.timeSheets) {
            is Success -> {
                timeSheets = it.timeSheets.invoke()
                Log.d("timesheet", "invalidate: ${timeSheets}")
                val trackedDays: MutableList<Long> = mutableListOf()

                for (timeSheet in timeSheets!!) {
                    val longDate = timeSheet.dateAttendance.convertToMillisFormat(
                        dateIso8601Format
                    )
                    longDate.let { longDate ->
                        trackedDays.add(longDate)
                    }
                }
                setTrackedDates(trackedDays)
                handleDataForPieChart(trackedDays,0)
                views.contentStatistical.statisticsRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                    when (checkedId) {
                        R.id.monthRadioButton -> {
                            handleDataForPieChart(trackedDays,STATISTICS_FOR_MONTH)
                        }
                        R.id.sixmonthsRadioButton -> {
                            handleDataForPieChart(trackedDays, STATISTICS_FOR_SIX_MONTHS)
                        }
                        R.id.yearRadioButton -> {
                            handleDataForPieChart(trackedDays, STATISTICS_FOR_YEAR)
                        }
                    }
                }
            }
        }
    }

    private fun handleCurrentUser(it:HomeViewState) {
        when (it.userCurrent) {
            is Success -> {
                it.userCurrent.invoke().let { user ->
                    mUser = user
                    setupUi()
                }
            }
        }
    }
    //-------------------------End of invalidate--------------------------------------------------//


    //-------------------------Setup Calendar-----------------------------------------------------//
    private fun setTrackedDates(trackedDays: List<Long>) {
        val calendarView = views.compactcalendarView
        val greenColor = Color.parseColor("#388E3C")
        calendarView.removeAllEvents()
        for (timestamp in trackedDays) {
            val event = Event(greenColor, timestamp)
            calendarView.addEvent(event)
        }
    }
    //-------------------------End of setup Calendar----------------------------------------------//


    //-------------------------Support Check-in---------------------------------------------------//
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
    //-------------------------End of support Check-in--------------------------------------------//
    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

}

