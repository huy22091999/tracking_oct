package com.oceantech.tracking.ui.timesheet

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.databinding.FragmentTimeSheetBinding
import com.skyhope.eventcalenderlibrary.model.Event
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
        views.btnCheckin.setOnClickListener {
            val event1 = Event(System.currentTimeMillis(), "OK", Color.RED)
            Log.d("calenderEvent", System.currentTimeMillis().toString())
            calenderEvent.addEvent(event1)
        }
        calenderEvent.initCalderItemClickCallback { dayContainerModel ->
            val event1 = Event(dayContainerModel.day.toLong(), "OK", Color.RED)
            calenderEvent.addEvent(event1)
            Log.d("calenderEvent", dayContainerModel.date)
        }
    }
}