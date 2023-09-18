package com.oceantech.tracking.ui.timesheet

import android.content.Context
import androidx.core.content.ContextCompat
import com.oceantech.tracking.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class SelectedDateDecorator(
    private val context: Context,
    private val selectedDates: MutableList<CalendarDay>
) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return selectedDates.contains(day)
    }

    override fun decorate(view: DayViewFacade?) {
        ContextCompat.getDrawable(context, R.drawable.circular_selected_bg)
            ?.let { view?.setBackgroundDrawable(it) }
    }

    // Function to add a new selected date
    fun addSelectedDate(date: CalendarDay) {
        if (!selectedDates.contains(date)) {
            selectedDates.add(date)
        }
    }
}