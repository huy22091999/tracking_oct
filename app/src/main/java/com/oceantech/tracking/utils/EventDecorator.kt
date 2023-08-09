package com.oceantech.tracking.utils

import androidx.compose.ui.graphics.Color
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan

class EventDecorator(private val date: CalendarDay) : DayViewDecorator {
    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return date == day
    }

    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(DotSpan(5.0F, 0xFFFF0000.toInt()))
    }
}