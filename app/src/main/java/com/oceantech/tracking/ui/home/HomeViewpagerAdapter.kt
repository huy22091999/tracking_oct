package com.oceantech.tracking.ui.home

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.oceantech.tracking.data.model.ItemTab
import com.oceantech.tracking.ui.menu.MenuFragment
import com.oceantech.tracking.ui.timesheet.TimeSheetFragment
import com.oceantech.tracking.ui.tracking.TrackingFragment
import com.oceantech.tracking.ui.users.UsersFragment

class HomeViewpagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle)
    : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> return TrackingFragment()
            1 -> return TimeSheetFragment()
            2 -> return UsersFragment()
            3 -> return MenuFragment()
        }

        return TrackingFragment();
    }
}