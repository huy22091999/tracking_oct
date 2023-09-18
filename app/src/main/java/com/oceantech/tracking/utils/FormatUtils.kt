package com.oceantech.tracking.utils

import android.widget.Toast
import com.prolificinteractive.materialcalendarview.CalendarDay
import java.text.SimpleDateFormat
import java.util.*


object StringUltis {
    val dateIso8601Format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
}


fun String.convertToCalendarDay(inputDateFormat: SimpleDateFormat): CalendarDay? {
    try {
        val date = inputDateFormat.parse(this)
        date?.let {
            val calendar = Calendar.getInstance()
            calendar.time = it

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1 // Tháng bắt đầu từ 0
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            return CalendarDay.from(year, month, day)
        }
    } catch (e: Exception) {
        println("Có lỗi khi chuyển đổi dateIso8601Format")
    }
    return null
}