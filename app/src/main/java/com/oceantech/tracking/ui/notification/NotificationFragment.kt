package com.oceantech.tracking.ui.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.databinding.FragmentNotificationBinding

class NotificationFragment : TrackingBaseFragment<FragmentNotificationBinding>() {
    private lateinit var menu: Menu
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setHasOptionsMenu(true)
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNotificationBinding {
        return FragmentNotificationBinding.inflate(inflater, container, false)
    }
//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        this.menu = menu
//        inflater.inflate(R.menu.edt_info_menu, menu)
//        val editMenuItem = menu.findItem(R.id.action_edit)
//        val saveMenuItem = menu.findItem(R.id.action_save)
//        // Ban đầu ẩn menu item "Lưu"
//        saveMenuItem.isVisible = false
//        super.onCreateOptionsMenu(menu, inflater)
//    }

}