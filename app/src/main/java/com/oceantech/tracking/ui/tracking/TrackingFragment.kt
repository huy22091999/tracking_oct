package com.oceantech.tracking.ui.tracking

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.FragmentTrackingBinding
import com.oceantech.tracking.ui.tracking.adapter.TrackingAdapter


class TrackingFragment : TrackingBaseFragment<FragmentTrackingBinding>() {
    val viewModel: TrackingSubViewModel by activityViewModel()
    lateinit var adapter: TrackingAdapter
    private var mUser: User? = null
    var state: Int = 0

    companion object {
        private const val GET_CURUSER= 1
        private const val GET_ALL = 2
        private const val SAVE = 3
    }

    override fun getBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentTrackingBinding {
        return FragmentTrackingBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.handle(TrackingViewAction.GetCurrentUser)
        state= GET_CURUSER
        setupUi();
        viewModel.observeViewEvents {
            handleEvent(it)
        }
        views.btnAddTracking.setOnClickListener {
            viewModel.handle(TrackingViewAction.NavigateToAddDialog)
        }
        parentFragmentManager.setFragmentResultListener("requestKey", this) { _, bundle ->
            val receivedTracking = bundle.getSerializable("key_tracking") as Tracking
            viewModel.handle(TrackingViewAction.PostNewTracking(receivedTracking))
            state = SAVE
        }
    }

    private fun setupUi() {

        adapter = TrackingAdapter {
            val action =
                TrackingFragmentDirections.actionNavTrackingFragmentToDetailTrackingFragment(it)
            findNavController().navigate(action)
        }
        views.rcyTracking.adapter = adapter
        viewModel.handle(TrackingViewAction.GetAllTrackingByUser)
        state = GET_ALL
    }

    private fun handleEvent(it: TrackingViewEvent) {
        when (it) {
            is TrackingViewEvent.ResetLanguege -> {
                views.trackingContent.text = getString(R.string.tracking_content)
            }

            is TrackingViewEvent.NavigateToAddDialog -> {
                    withState(viewModel){
                        it.asyncCurrentUser.invoke().let {
                            if (it!=null){
                            val action =
                                TrackingFragmentDirections.actionNavTrackingFragmentToAddTrackingFragment(it)
                            findNavController().navigate(action)
                        }
                    }
                }
            }
        }
    }

    private fun handleStateGetALl(it: TrackingViewState) {
        when (it.asyncTrackingArray) {
            is Success -> {
                it.asyncTrackingArray.invoke().let {
                    adapter.setData(it)
                }
            }

            is Fail -> {
            
            }
        }
    }

    private fun handleStateSaved(it: TrackingViewState) {
        when (it.asyncSaveTracking) {
            is Success -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.add_tracking_successfully),
                    Toast.LENGTH_LONG
                ).show()
                viewModel.handle(TrackingViewAction.GetAllTrackingByUser)
                this.state = GET_ALL
            }

            is Fail -> {
                it.asyncSaveTracking.error.message.let {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.add_tracking_unsuccessfully),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
    private fun handleStateGetCurrentUser(it: TrackingViewState) {
        when (it.asyncCurrentUser) {
            is Success -> {
                it.asyncCurrentUser.invoke().let {
                    Log.d("user", "handleStateGetCurrentUser: ${mUser}")
                    mUser=it
                }
            }

            is Fail -> {

            }
        }
    }
    override fun invalidate(): Unit = withState(viewModel) {
        when (state) {
            GET_CURUSER -> handleStateGetCurrentUser(it)
            GET_ALL -> handleStateGetALl(it)
            SAVE -> handleStateSaved(it)
        }
    }




}