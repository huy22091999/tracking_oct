package com.oceantech.tracking.ui.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
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
import com.oceantech.tracking.data.model.Notification
import com.oceantech.tracking.databinding.FragmentNotificationBinding
import com.oceantech.tracking.ui.tracking.TrackingAdapter
import com.oceantech.tracking.utils.checkStatusApiRes

class NotificationFragment : TrackingBaseFragment<FragmentNotificationBinding>() {
    private lateinit var notificationAdapter: NotificationAdapter
    private val mlistNotification: MutableList<Notification> = mutableListOf()

    private val notificationViewModel: NotificationViewModel by activityViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNotificationBinding {
        return FragmentNotificationBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notificationViewModel.handle(NotificationViewAction.getAllNotification)
        setUpAdapter()
        clickUi()
    }

    private fun clickUi() {
        views.itemLoading.retryButton.setOnClickListener {
            notificationViewModel.handle(NotificationViewAction.getAllNotification)
        }
    }

    private fun setUpAdapter() {
        notificationAdapter = NotificationAdapter(requireActivity(), mlistNotification)
        views.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        views.recyclerView.adapter = notificationAdapter
    }

    override fun invalidate(): Unit = withState(notificationViewModel) {
        when (it.mListnotification) {
            is Success -> {
                views.itemLoading.itemLoadingState.visibility = View.GONE
                Toast.makeText(requireActivity(), R.string.success, Toast.LENGTH_SHORT).show()
                mlistNotification.clear()
                mlistNotification.addAll(it.mListnotification.invoke())
                notificationAdapter.notifyDataSetChanged()
            }

            is Fail -> {
                Toast.makeText(
                    requireContext(),
                    getString(checkStatusApiRes(it.mListnotification)),
                    Toast.LENGTH_SHORT
                ).show()
                views.itemLoading.retryButton.visibility = View.VISIBLE
            }

            else -> {
                false
            }
        }
    }

}