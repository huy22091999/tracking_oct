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
    private var offline1 = false

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return selectedDates.contains(day)
    }

    override fun decorate(view: DayViewFacade?) {
        val backgroundDrawableResId = if (offline1) {
            R.drawable.circular_selected_bg // Màu xanh cho trạng thái offline
        } else {
            R.drawable.circular_selected_bg_red // Màu đỏ cho trạng thái không offline
        }

        ContextCompat.getDrawable(context, backgroundDrawableResId)
            ?.let { view?.setBackgroundDrawable(it) }
    }

    // Function to add a new selected date
    fun addSelectedDate(offline: Boolean, date: CalendarDay) {
        if (!selectedDates.contains(date)) {
            selectedDates.add(date)
            offline1 = offline
        }
    }
}