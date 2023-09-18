package com.oceantech.tracking.ui.timesheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.airbnb.mvrx.activityViewModel
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.databinding.FragmentTimeSheetBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import javax.inject.Inject


class TimeSheetFragment @Inject constructor() : TrackingBaseFragment<FragmentTimeSheetBinding>() {

    val mViewModel: TimeSheetViewModel by activityViewModel()
    private val selectedDates: MutableList<CalendarDay>? = null
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

        listennerClickUI()

        val decorator = selectedDates?.let { SelectedDateDecorator(requireActivity(), it) }
    }
        views.calenderEvent.addDecorators(decorator)
    }

    private fun listennerClickUI() {
        views.btnCheckin.setOnClickListener {
            val decorator = SelectedDateDecorator(requireActivity(), selectedDates)
            val currentDate = CalendarDay.today()
            decorator.addSelectedDate(currentDate)
            calenderEvent.invalidateDecorators()
        }
    }
}