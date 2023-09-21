package com.oceantech.tracking.ui.tracking

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
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
    private var positionToSelected: Int = -1

    private lateinit var rvAdapter: TrackingAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        trackingViewModel.observeViewEvents {
            if (it != null) {
                handleEvents(it)
            }
        }
    }

    private fun handleEvents(it: TrackingViewEvent) {
        when (state) {
            DEFAULT_STATE -> handleGetAll(it)
            SAVE -> handleSave(it)
            DELETE -> handleDelete(it)
            UPDATE -> handleUpdate(it)
        }
    }

    private fun handleUpdate(it: TrackingViewEvent) {
        when (it) {
            is TrackingViewEvent.ReturnGetTracking -> {
                withState(trackingViewModel) {
                    if (it.updateTracking is Success) {
                        it.updateTracking.invoke().content?.let { it1 ->
                            rvAdapter.update(
                                it1, positionToSelected
                            )
                        }
                        state = NONE
                    }
                }
            }

            else -> {
                false
            }
        }
    }

    private fun handleGetAll(it: TrackingViewEvent) {
        when (it) {
            is TrackingViewEvent.ReturnGetTracking -> {
                withState(trackingViewModel) {
                    if (it.listTracking is Success) {
                        mlistTracking.clear()
                        mlistTracking.addAll(it.listTracking.invoke())
                        rvAdapter.notifyDataSetChanged()
                        state = NONE
                    }
                }
            }

            else -> {
                false
            }
        }
    }

    private fun handleDelete(it: TrackingViewEvent) {
        when (it) {
            is TrackingViewEvent.ReturnDeleteTracking -> {
                withState(trackingViewModel) {
                    if (it.deleteTracking is Success) if (positionToSelected != -1) {
                        rvAdapter.removeItem(positionToSelected)
                        state = NONE
                    }
                }
            }

            else -> {
                false
            }
        }
    }

    private fun handleSave(it: TrackingViewEvent) {
        when (it) {
            is TrackingViewEvent.ReturnSaveTracking -> {
                withState(trackingViewModel) {
                    if (it.Tracking is Success) {
                        rvAdapter.addItem(it.Tracking.invoke())
                        state = NONE
                    }
                }
            }

            else -> {
                false
            }
        }
    }

    override fun getBinding(
        inflater: LayoutInflater, container: ViewGroup?
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

        views.imgNotification.setOnClickListener {
            findNavController().navigate(R.id.action_nav_trackingFragment_to_notificationFragment)
        }
        views.btnCheckin.setOnClickListener {
            state = NONE
            views.btnCheckin.visibility = View.GONE
            views.layoutTracking.visibility = View.VISIBLE
            views.btnSendFeedback.visibility = View.VISIBLE
            views.btnUpdateFeedback.visibility = View.GONE
            val editableText =
                Editable.Factory.getInstance().newEditable(LocalDateTime.now().toString())
            views.edtTracking.text = editableText
        }
        views.btnSendFeedback.setOnClickListener {
            content = views.edtTracking.text.toString().trim()
            if (content.isNullOrEmpty()) {
                AlertDialog.Builder(requireContext()).setTitle(R.string.channel_description)
                    .setMessage(R.string.feedback_empty).setNegativeButton(R.string.ok, null).show()
            } else if (!content.isNullOrEmpty()) {
                state = SAVE
                trackingViewModel.handle(
                    TrackingViewAction.saveTracking(
                        Tracking(
                            content, LocalDateTime.now().toString()
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
            state = NONE
        }
    }

    private fun setUpRcv() {
        rvAdapter = TrackingAdapter(requireActivity(), mlistTracking, action)
        views.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        views.recyclerView.adapter = rvAdapter
    }

    private val action: (Tracking, Int, ActionType) -> Unit = { tracking, position, actionType ->
        if (actionType == ActionType.DELETE) {
            tracking.id?.let { TrackingViewAction.deleteTracking(it) }?.let {
                state = DELETE
                trackingViewModel.handle(it)
            }

        }
        if (actionType == ActionType.EDIT) {
            views.btnCheckin.visibility = View.GONE
            views.layoutTracking.visibility = View.VISIBLE
            views.btnSendFeedback.visibility = View.GONE
            views.btnUpdateFeedback.visibility = View.VISIBLE
            editTracking(tracking, position)
        }
        positionToSelected = position
    }

    private fun editTracking(tracking: Tracking, position: Int) {
        val editableText = Editable.Factory.getInstance().newEditable(tracking.content)
        views.edtTracking.text = editableText
        views.btnUpdateFeedback.setOnClickListener {
            content = views.edtTracking.text.toString().trim()
            if (content.isNullOrEmpty()) {
                AlertDialog.Builder(requireContext()).setTitle(R.string.channel_description)
                    .setMessage(R.string.feedback_empty).setNegativeButton(R.string.ok, null).show()
            } else if (!content.isNullOrEmpty()) {

                tracking.id?.let { it1 ->
                    TrackingViewAction.upadateTracking(
                        it1, content
                    )
                }?.let { it2 ->
                    trackingViewModel.handle(
                        it2
                    )
                    state = UPDATE
                }
                views.btnCheckin.visibility = View.VISIBLE
                views.layoutTracking.visibility = View.GONE
            }
        }
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
                trackingViewModel.handleReturnGetAll(it.listTracking.invoke())
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
                Log.d("Save tracking success", it.Tracking.invoke().toString())
                trackingViewModel.handleReturnSave(it.Tracking.invoke())
            }

            is Fail -> {
                Toast.makeText(
                    requireContext(), getString(checkStatusApiRes(it.Tracking)), Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                false
            }
        }

        when (it.deleteTracking) {
            is Success -> {
                Log.d("Delete tracking success", it.deleteTracking.invoke().toString())
                trackingViewModel.handleReturnDelete()
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

        when (it.updateTracking) {
            is Success -> {
                Log.d("Update tracking success", it.updateTracking.invoke().toString())
                trackingViewModel.handleReturnUpdate(
                    it.updateTracking.invoke().content, positionToSelected
                )
            }

            is Fail -> {
                Toast.makeText(
                    requireContext(),
                    getString(checkStatusApiRes(it.updateTracking)),
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                false
            }
        }
    }
//    override fun onStart() {
//        super.onStart()
//        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
//    }
//    override fun onDestroyView() {
//        super.onDestroyView()
//        (requireActivity() as AppCompatActivity).supportActionBar?.show()
//    }

    companion object {
        private const val DEFAULT_STATE = 0
        private const val DELETE = 1
        private const val UPDATE = 2
        private const val SAVE = 3
        private const val NONE = -1

    }

    private var state = DEFAULT_STATE

}