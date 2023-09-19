package com.oceantech.tracking.ui.tracking

import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.text.style.TextAppearanceSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.databinding.FragmentTrackingBinding
import com.oceantech.tracking.utils.checkStatusApiRes
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import org.threeten.bp.LocalDate
import java.text.SimpleDateFormat
import java.util.Locale

@Suppress("DEPRECATION")
class TrackingFragment : TrackingBaseFragment<FragmentTrackingBinding>() {

    val trackingViewModel: TrackingViewModel by activityViewModel()
    private var mlistTracking: MutableList<Tracking> = mutableListOf()

    private lateinit var rvAdapter: TrackingAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTrackingBinding {
        return FragmentTrackingBinding.inflate(inflater, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpCalender()
        setUpRcv()
        trackingViewModel.handle(TrackingViewAction.getTrackingAction)
    }

    private fun setUpRcv() {
        rvAdapter = TrackingAdapter(mlistTracking)
        views.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        views.recyclerView.adapter = rvAdapter
    }

    private fun setUpCalender() {
        views.calenderEvent.state().edit().setCalendarDisplayMode(CalendarMode.WEEKS).commit()
        val currentDate = CalendarDay.today()
        val selectedDateDecorator = object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay?): Boolean {
                return day == currentDate
            }

            override fun decorate(view: DayViewFacade?) {
                view?.addSpan(ForegroundColorSpan(resources.getColor(R.color.red)))
            }
        }
        views.calenderEvent.addDecorators(selectedDateDecorator)
    }

    override fun invalidate(): Unit = withState(trackingViewModel) {
        when (it.listTracking) {
            is Success -> {
                //listTracking= it.listTracking.invoke() as MutableList<Tracking>
                mlistTracking.addAll(it.listTracking.invoke())
                rvAdapter.notifyDataSetChanged()
            }

            is Fail -> {
                Toast.makeText(
                    requireContext(),
                    getString(checkStatusApiRes(it.listTracking)),
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                false
            }
        }
    }

}