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

fun List<String>.convertToDateTimePartsList(inputDateFormat: SimpleDateFormat): List<Pair<String, String>> {
    val dateTimePartsList = mutableListOf<Pair<String, String>>()
    for (it in this) {
        try {
            val date = inputDateFormat.parse(it)
            date?.let {
                val calendar = Calendar.getInstance()
                calendar.time = it

                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH) + 1 // Tháng bắt đầu từ 0
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)

                val datePart = String.format("%d-%02d-%02d", year, month, day)
                val timePart = String.format("%02d:%02d", hour, minute)

                val dateTimePart = Pair(datePart, timePart)
                dateTimePartsList.add(dateTimePart)
            }
        } catch (e: Exception) {
            println("Có lỗi khi chuyển đổi dateIso8601Format")
        }
    }

    return dateTimePartsList
}
