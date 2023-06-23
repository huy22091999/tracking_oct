package com.oceantech.tracking.ui.tracking

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.databinding.FragmentTrackingBinding
import com.oceantech.tracking.ui.tracking.adapter.TrackingAdapter
import com.oceantech.tracking.ui.item_decoration.ItemDecoration
import com.oceantech.tracking.utils.setupRecycleView
import com.oceantech.tracking.utils.showToast
import javax.inject.Inject

class TrackingFragment @Inject constructor() : TrackingBaseFragment<FragmentTrackingBinding>() {

    private val trackingViewModel: TrackingViewModel by activityViewModel()
    private lateinit var trackingRV: RecyclerView
    private lateinit var trackingAdapter: TrackingAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //get all tracking when fragment is creating status
        trackingViewModel.handle(TrackingViewAction.GetAllTracking())
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTrackingBinding {
        return FragmentTrackingBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trackingRV = views.trackingRV
        trackingAdapter = TrackingAdapter(deleteTracking = {
            trackingViewModel.handle(TrackingViewAction.DeleteTracking(it))
        }, updateTracking = { tracking, id ->
            trackingViewModel.handle(TrackingViewAction.UpdateTracking(tracking, id))
        })

        setupRecycleView(trackingRV, trackingAdapter, requireContext())

        views.trackingFAB.setOnClickListener {
            saveTracking()
        }

    }

    @SuppressLint("InflateParams") // to take null as view group
    private fun saveTracking() {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.add_new_tracking, null)
        val alertDialog = AlertDialog.Builder(requireContext())
            .setCancelable(true)
            .setIcon(R.drawable.add_tracking_icon)
            .setTitle(getString(R.string.add_tracking))
            .setView(view)
            .setPositiveButton(getString(R.string.add_new_tracking), null)
            .setNegativeButton(getString(R.string.cancel_add_new_tracking), null)
            .create()
        alertDialog.show()

        // make alert dialog not dismiss when content is not typed
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val content = view.findViewById<EditText>(R.id.edtContent).text.toString()
            if (content.isEmpty()) {
                showToast(requireContext(), getString(R.string.requirement_add_new_tracking))
            } else {
                trackingViewModel.handle(
                    TrackingViewAction.SaveTracking(content)
                )
                alertDialog.dismiss()
            }

        }
    }

    @SuppressLint("NotifyDataSetChanged", "LogNotTimber")
    override fun invalidate(): Unit = withState(trackingViewModel) {
        when (it.getAllTracking) {
            is Loading -> {
                views.trackingPB.visibility = View.VISIBLE
                views.trackingRV.visibility = View.GONE
                views.trackingFAB.visibility = View.GONE
            }

            is Success -> {
                views.trackingPB.visibility = View.GONE
                views.trackingRV.visibility = View.VISIBLE
                views.trackingFAB.visibility = View.VISIBLE
                it.getAllTracking.invoke()?.let { listTracking ->
                    trackingAdapter.setListTracking(listTracking)
                    trackingAdapter.notifyDataSetChanged()
                }
            }

            is Fail -> {
                showToast(requireContext(), getString(R.string.get_tracking_list_failed))
                Log.i("Tracking", (it.getAllTracking as Fail<List<Tracking>>).error.toString())
            }
        }
        when (it.saveTracking) {
            is Success -> {
                showToast(requireContext(), getString(R.string.check_in_tracking_successfully))
                it.saveTracking = Uninitialized
                trackingViewModel.handle(TrackingViewAction.GetAllTracking())
            }

            is Fail -> {
                showToast(requireContext(), getString(R.string.check_in_tracking_failed))
                Log.i("Tracking", (it.saveTracking as Fail<Tracking>).error.toString())
            }
        }
        when (it.deleteTracking) {
            is Success -> {
                showToast(requireContext(), getString(R.string.delete_tracking_successfully))
                it.deleteTracking = Uninitialized
                trackingViewModel.handle(TrackingViewAction.GetAllTracking())
            }

            is Fail -> {
                showToast(requireContext(), getString(R.string.delete_tracking_failed))
                Log.i("Tracking", (it.deleteTracking as Fail<Tracking>).error.toString())
            }
        }
    }
}