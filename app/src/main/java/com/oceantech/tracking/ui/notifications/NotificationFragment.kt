package com.oceantech.tracking.ui.notifications

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.databinding.FragmentNotificationBinding
import com.oceantech.tracking.ui.notifications.adapter.NotificationAdapter
import com.oceantech.tracking.utils.checkError
import com.oceantech.tracking.utils.registerNetworkReceiver
import com.oceantech.tracking.utils.setupRecycleView

@RequiresApi(Build.VERSION_CODES.O)
class NotificationFragment : TrackingBaseFragment<FragmentNotificationBinding>() {

    private val notificationViewModel: NotificationViewModel by activityViewModel()
    private lateinit var notificationAdapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerNetworkReceiver {
            notificationViewModel.handle(NotificationViewAction.GetNotifications)
        }
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNotificationBinding {
        return FragmentNotificationBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notificationAdapter = NotificationAdapter()
        setupRecycleView(views.notiRV, notificationAdapter, requireContext())
        notificationViewModel.onEach {
            views.notiPB.isVisible = it.isLoading() || it.getNotification is Fail
            views.notiRV.isVisible = it.getNotification is Success
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun invalidate(): Unit = withState(notificationViewModel){
        when(val noti = it.getNotification){
            is Success -> {
                notificationAdapter.list = noti.invoke()
                notificationAdapter.notifyDataSetChanged()
            }
            is Fail -> {
                noti.error.message?.let { error ->
                    checkError(error)
                }
            }
            else -> {}
        }
    }
}