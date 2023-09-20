package com.oceantech.tracking.utils

import android.util.Log
import android.widget.Toast
import com.oceantech.tracking.utils.StringUltis.outputDateDateformat
import com.oceantech.tracking.utils.StringUltis.outputTimeDateFormat
import com.prolificinteractive.materialcalendarview.CalendarDay
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*


object StringUltis {
    val dateIso8601Format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    val outputDateDateformat = SimpleDateFormat("dd/MM/yyyy")
    val outputTimeDateFormat = SimpleDateFormat("HH:mm")
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

fun List<String>.convertToDateTimePartsList(): List<Pair<String, String>> {
    val dateTimePartsList = mutableListOf<Pair<String, String>>()
    for (it in this) {
        try {
            //inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = ZonedDateTime.parse(it)
            date?.let {
                Log.d("Parsed Date", it.toString())

                // Định dạng lại thời gian thành "dd/MM/yyyy" và "HH:mm"
                val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val datePart = date.format(dateFormatter)

                val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
                val timePart = date.format(timeFormatter)

                val dateTimePart = Pair(datePart, timePart)
                dateTimePartsList.add(dateTimePart)
            }
        } catch (e: Exception) {
            println("Có lỗi khi chuyển đổi định dạng")
        }
    }

    return dateTimePartsList
}
