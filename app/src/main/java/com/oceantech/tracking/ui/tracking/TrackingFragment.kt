package com.oceantech.tracking.ui.tracking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.Notify
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.FragmentTrackingBinding
import com.oceantech.tracking.ui.home.HomeViewModel
import com.oceantech.tracking.ui.tracking.adapter.TrackingAdapter
import com.oceantech.tracking.utils.NotificationDialogFragment
import com.oceantech.tracking.utils.showDialog
//done
class TrackingFragment : TrackingBaseFragment<FragmentTrackingBinding>() {
    val viewModel: TrackingSubViewModel by activityViewModel()
    private val homeViewModel: HomeViewModel by activityViewModel()
    //data
    private var mUser: User?=null
    var state: Int = 0
    //views
    lateinit var adapter: TrackingAdapter
    companion object {
        private const val GET_ALL = 1
        private const val SAVE = 2
    }

    override fun getBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentTrackingBinding {
        return FragmentTrackingBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi();
        listenEvent()
    }

    private fun listenEvent() {
        views.btnAddTracking.setOnClickListener {
            findNavController().navigate(R.id.action_nav_trackingFragment_to_addTrackingFragment)
        }
        parentFragmentManager.setFragmentResultListener("requestKey", this) { _, bundle ->
            val receivedTracking = bundle.getSerializable("key_tracking") as Tracking
            viewModel.handle(TrackingViewAction.PostNewTracking(receivedTracking))
            state = SAVE
        }
    }

    private fun setupUi() {
        withState(homeViewModel){
            it.userCurrent.invoke().let { user ->
                mUser=user
                views.displayName.text = "${user?.displayName}"
                views.levelLabel.text= getString(R.string.year) +" ${user?.year}"
            }
        }
        adapter = TrackingAdapter {
            val action =
                TrackingFragmentDirections.actionNavTrackingFragmentToDetailTrackingFragment(it)
            findNavController().navigate(action)
        }
        views.rcyTracking.adapter = adapter
        viewModel.handle(TrackingViewAction.GetAllTrackingByUser)
        state = GET_ALL
    }

    private fun handleStateGetALl(it: TrackingViewState) {
        when (it.asyncTrackingArray) {
            is Success -> {
                it.asyncTrackingArray.invoke().let {
                    if (it.isEmpty()){
                        setupTrackingNotFound()
                    }
                    adapter.setData(it)
                }
            }
        }
    }

    private fun setupTrackingNotFound() {
        views.noTracking.visibility=View.VISIBLE
    }

    private fun handleStateSaved(it: TrackingViewState) {
        when (it.asyncSaveTracking) {
            is Success -> {
                val notify= Notify(NotificationDialogFragment.SUCCESS_ID,getString(R.string.add_tracking_successfully))
                showDialog(notify,childFragmentManager)
                viewModel.handle(TrackingViewAction.GetAllTrackingByUser)
                this.state = GET_ALL
            }

            is Fail -> {
                it.asyncSaveTracking.error.message.let {
                    val notify= Notify(NotificationDialogFragment.FALURE_ID,getString(R.string.add_tracking_unsuccessfully))
                    showDialog(notify,childFragmentManager)
                }
            }
        }
    }
    override fun invalidate(): Unit = withState(viewModel) {
        when (state) {
            GET_ALL -> handleStateGetALl(it)
            SAVE -> handleStateSaved(it)
        }
    }




}