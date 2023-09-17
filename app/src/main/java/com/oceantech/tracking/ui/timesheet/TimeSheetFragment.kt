package com.oceantech.tracking.ui.timesheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.databinding.FragmentTimeSheetBinding
import com.prolificinteractive.materialcalendarview.CalendarDay
import javax.inject.Inject


class TimeSheetFragment @Inject constructor() : TrackingBaseFragment<FragmentTimeSheetBinding>() {

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
        val calenderEvent = views.calenderEvent
        val selectedDates = arrayListOf(
            CalendarDay.from(2023, 9, 16), // Ví dụ: Ngày 17/9/2023
            CalendarDay.from(2023, 9, 15), // Ví dụ: Ngày 18/9/2023
            // Thêm các ngày khác vào danh sách
        )
        val decorator = SelectedDateDecorator(requireActivity(), selectedDates)
        calenderEvent.addDecorators(decorator)
        views.btnCheckin.setOnClickListener {
            val currentDate = CalendarDay.today()
            decorator.addSelectedDate(currentDate)
            calenderEvent.invalidateDecorators()
        }
        // Đặt sự kiện click cho các ngày
        calenderEvent.setOnDateChangedListener { widget, date, selected ->
            // Hiển thị toast với ngày đã bấm
            Toast.makeText(
                requireActivity(),
                "Bạn đã chọn ngày: ${date.day}/${date.month}/${date.year}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}