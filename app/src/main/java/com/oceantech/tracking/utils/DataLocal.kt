package com.oceantech.tracking.utils

import android.content.Context
import com.oceantech.tracking.R
import com.oceantech.tracking.data.model.ItemTab
import com.oceantech.tracking.data.model.Menu
import com.oceantech.tracking.data.model.Menu2
import com.oceantech.tracking.ui.menu.MenuAdapter.Companion.TYPE_RADIO
import com.oceantech.tracking.ui.menu.MenuAdapter.Companion.TYPE_TEXT
import com.oceantech.tracking.ui.menu.MenuAdapter.Companion.TYPE_SWITCH
import io.reactivex.Observable

fun getTabLayoutLocal(): Observable<List<ItemTab>> {
    var list = ArrayList<ItemTab>()
    list.add(ItemTab("0", "Tracking", R.drawable.baseline_checklist_24, R.drawable.baseline_checklist_24_1))
    list.add(ItemTab("1", "Check in", R.drawable.baseline_check_24, R.drawable.baseline_check_24_1))
    list.add(ItemTab("2", "User", R.drawable.baseline_supervised_user_circle_24, R.drawable.baseline_supervised_user_circle_24_1))
    list.add(ItemTab("3", "Profile", R.drawable.baseline_person_24, R.drawable.baseline_person_24_1))
    return Observable.just(list)
}
fun getListSuggestTracking(): List<String> {
    var list = ArrayList<String>()
    list.add("Hoàn thành công việc")
    list.add("Học được kỹ năng mới")
    list.add("Hỗ trợ đồng nghiệp")
    list.add("Vượt qua bài đánh giá")
    list.add("Nói chuyện cùng em tester")
    list.add("Hoàn thành chức năng")
    list.add("Lên được kế hoạch")
    return list
}

fun getMenus(context: Context): ArrayList<Menu>{
    val list = ArrayList<Menu>()
    list.add(Menu("0", R.drawable.baseline_auto_mode_24, context.getString(R.string.mode), TYPE_SWITCH))
    list.add(Menu("1", R.drawable.ic_language, context.getString(R.string.language), TYPE_TEXT))
    return list
}
fun getMenuLanguage(context: Context): ArrayList<Menu>{
    val list = ArrayList<Menu>()
    list.add(Menu("en", R.drawable.img_flag_en, context.getString(R.string.en), TYPE_RADIO))
    list.add(Menu("vi", R.drawable.img_flag_vi, context.getString(R.string.vi), TYPE_RADIO))
    return list
}

fun getOptionInfo(context: Context, isActive: Boolean): ArrayList<Menu2>{
    val list = ArrayList<Menu2>()
    list.add(Menu2(0, R.drawable.baseline_password_24, context.getString(R.string.change_pass)))
    if (isActive){
        list.add(Menu2(1, R.drawable.baseline_block_24, context.getString(R.string.block_user)))
    }else{
        list.add(Menu2(2, R.drawable.baseline_block_24, context.getString(R.string.unblock_user)))
    }

    return list
}


