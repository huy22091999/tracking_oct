package com.oceantech.tracking.core

import android.view.View
import com.oceantech.tracking.data.model.Menu
import com.oceantech.tracking.data.model.Menu2
import com.oceantech.tracking.data.model.Tracking

abstract class TrackingClickItem {

    open fun onItemTrackingOptionMenuClickListenner(view: View, tracking: Tracking) {}
    open fun onItemTrackingAddClickListenner(){}
    open fun onItemAvatarClickListennner(){}
    open fun onItemPosition(position: Int){}

    open fun onItemMenu2ClickListenner(menu2: Menu2){}
    open fun onItemMenu1ClickListenner(menu1: Menu){}
    open fun onSwitchMenu1ClickListenner(isBoolean: Boolean){}

    open fun onItemUserClickListenner(userId: String){}
    open fun onItemSearchUserClickListenner(){}

}