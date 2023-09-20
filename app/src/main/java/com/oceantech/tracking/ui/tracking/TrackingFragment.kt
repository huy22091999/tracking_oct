package com.oceantech.tracking.ui.tracking

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import java.time.LocalDateTime

@Suppress("DEPRECATION")
class TrackingFragment : TrackingBaseFragment<FragmentTrackingBinding>() {

    val trackingViewModel: TrackingViewModel by activityViewModel()
    private var mlistTracking: MutableList<Tracking> = mutableListOf()
    private var content: String = ""
    private var positionToDelete: Int = -1

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
        clickUi()
        trackingViewModel.handle(TrackingViewAction.getTrackingAction)
    }

    private fun clickUi() {
        views.btnCheckin.setOnClickListener {
            views.btnCheckin.visibility = View.GONE
            views.layoutTracking.visibility = View.VISIBLE
            val editableText =
                Editable.Factory.getInstance().newEditable(LocalDateTime.now().toString())
            views.edtTracking.text = editableText

        }
        views.btnSendFeedback.setOnClickListener {
            content = views.edtTracking.text.toString().trim()
            if (content.isNullOrEmpty()) {
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.channel_description)
                    .setMessage(R.string.feedback_empty)
                    .setNegativeButton(R.string.ok, null)
                    .show()
            } else if (!content.isNullOrEmpty()) {
                trackingViewModel.handle(
                    TrackingViewAction.saveTracking(
                        Tracking(
                            content,
                            LocalDateTime.now().toString()
                        )
                    )
                )
                views.btnCheckin.visibility = View.VISIBLE
                views.layoutTracking.visibility = View.GONE
            }
        }
        views.btnCancel.setOnClickListener {
            views.btnCheckin.visibility = View.VISIBLE
            views.layoutTracking.visibility = View.GONE
        }
    }

    private fun setUpRcv() {
        rvAdapter = TrackingAdapter(requireActivity(), mlistTracking, action)
        views.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        views.recyclerView.adapter = rvAdapter
    }

    private val action: (Tracking, Int) -> Unit = { tracking, position ->
        tracking.id?.let { TrackingViewAction.deleteTracking(it) }
            ?.let { trackingViewModel.handle(it) }
        positionToDelete = position
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
                mlistTracking.clear()
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

        when (it.Tracking) {
            is Success -> {
                mlistTracking.add(it.Tracking.invoke())
                rvAdapter.notifyDataSetChanged()
            }

            is Fail -> {
                Toast.makeText(
                    requireContext(),
                    getString(checkStatusApiRes(it.Tracking)),
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                false
            }
        }

        when (it.deleteTracking) {
            is Success -> {
                if (positionToDelete != -1)
                    rvAdapter.removeItem(positionToDelete)
            }

            is Fail -> {
                Toast.makeText(
                    requireContext(),
                    getString(checkStatusApiRes(it.deleteTracking)),
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                false
            }
        }
    }

}