package com.oceantech.tracking.ui.tracking

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.databinding.FragmentTrackingBinding
import com.oceantech.tracking.ui.tracking.adapter.TrackingAdapter
import com.oceantech.tracking.utils.checkError
import com.oceantech.tracking.utils.setupRecycleView
import com.oceantech.tracking.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.http.GET
import javax.inject.Inject

@SuppressLint("NotifyDataSetChanged", "LogNotTimber")
@AndroidEntryPoint
class TrackingFragment @Inject constructor() : TrackingBaseFragment<FragmentTrackingBinding>() {

    private val trackingViewModel: TrackingViewModel by activityViewModel()
    private lateinit var trackingAdapter: TrackingAdapter

    companion object {
        private const val GET_ALL = 1
        private const val DELETE = 2
        private const val SAVE = 3
        private const val UPDATE = 4
    }

    private var state: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //get all tracking when fragment is creating status
        trackingViewModel.handle(TrackingViewAction.GetAllTracking())
        state = GET_ALL
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTrackingBinding {
        return FragmentTrackingBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trackingAdapter = TrackingAdapter(deleteTracking = {
            trackingViewModel.handle(TrackingViewAction.DeleteTracking(it))
            state = DELETE
        }, updateTracking = { tracking, id ->
            trackingViewModel.handle(TrackingViewAction.UpdateTracking(tracking, id))
            state = UPDATE
        })

        setupRecycleView(views.trackingRV, trackingAdapter, requireContext())

        trackingViewModel.onEach {
            views.trackingPB.isVisible = it.isLoading() || it.getAllTracking is Fail
            views.trackingRV.isVisible = !it.isLoading() && it.getAllTracking is Success
            views.trackingFAB.isVisible = !it.isLoading() && it.getAllTracking is Success
        }

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
                state = SAVE
                alertDialog.dismiss()
            }
        }
    }


    override fun invalidate(): Unit = withState(trackingViewModel) {
        when (state) {
            GET_ALL -> handleGetAllTracking(it)
            DELETE -> handleDeleteTracking(it)
            SAVE -> handleSaveTracking(it)
            UPDATE -> handleUpdateTracking(it)
        }
    }

    private fun handleGetAllTracking(state: TrackingViewState) {
        when (state.getAllTracking) {
            is Success -> {
                state.getAllTracking.invoke().let { listTracking ->
                    trackingAdapter.setListTracking(listTracking)
                    trackingAdapter.notifyDataSetChanged()
                }
            }

            is Fail -> {
                state.getAllTracking.error.message?.let {error ->
                    checkError(error)
                }
            }

            else -> {}
        }
    }

    private fun handleSaveTracking(state: TrackingViewState) {
        when (state.saveTracking) {
            is Success -> {
                showToast(requireContext(), getString(R.string.add_tracking_successfully))
                trackingViewModel.handle(TrackingViewAction.GetAllTracking())
                this.state = GET_ALL
            }

            is Fail -> {
                state.saveTracking.error.message?.let {error ->
                    checkError(error)
                }
            }

            else -> {}
        }
    }

    private fun handleDeleteTracking(state: TrackingViewState) {
        when (state.deleteTracking) {
            is Success -> {
                showToast(requireContext(), getString(R.string.delete_tracking_successfully))
                trackingViewModel.handle(TrackingViewAction.GetAllTracking())
                this.state = GET_ALL
            }

            is Fail -> {
                state.deleteTracking.error.message?.let {error ->
                    checkError(error)
                }
            }

            else -> {}
        }
    }

    private fun handleUpdateTracking(state: TrackingViewState) {
        when (state.updateTracking) {
            is Success -> {
                trackingViewModel.handle(TrackingViewAction.GetAllTracking())
                this.state = GET_ALL
            }

            is Fail -> {
                state.updateTracking.error.message?.let {error ->
                    checkError(error)
                }
            }

            else -> {}
        }
    }


}