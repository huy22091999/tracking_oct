package com.oceantech.tracking.ui.menu

import android.annotation.SuppressLint
import android.provider.MediaStore.Audio.Radio
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingClickItem
import com.oceantech.tracking.data.model.Menu
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.databinding.ItemMenuBinding
import com.oceantech.tracking.utils.changeMode

class MenuAdapter(val sessionManager: SessionManager, val callBack: TrackingClickItem) : RecyclerView.Adapter<MenuAdapter.ViewHolder>(){

    companion object{
        const val DEFAULT_ID = 0
        const val TYPE_SWITCH = 1
        const val TYPE_TEXT = 2
        const val TYPE_RADIO = 3
    }

    var list : List<Menu>? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setData(menus: List<Menu>?){
        if (menus != null){
            this.list = menus
            notifyDataSetChanged()
        }

    }

    inner class ViewHolder(private val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(menu: Menu){

            with(binding as ItemMenuBinding){
                binding.imgMode.setImageResource(menu.icon)
                binding.tvMode.text = menu.name
                handleView(menu.viewType, binding)

                if (menu.viewType == TYPE_SWITCH){
                val switchMode: SwitchCompat = binding.layoutMode.findViewById(DEFAULT_ID)
                sessionManager.fetchDarkMode().let { switchMode.isChecked = it ?: false }

                switchMode.setTrackResource(R.drawable.track_switch)
                switchMode.setThumbResource(R.drawable.thumb_switch)
                switchMode.setOnCheckedChangeListener { _, isChecked ->
                    changeMode(isChecked)
                    sessionManager.let { it.saveDarkMode(isChecked) }
                }
                }else if (menu.viewType == TYPE_TEXT){
                    val textView: TextView = binding.layoutMode.findViewById(DEFAULT_ID)
                    textView.text = sessionManager.fetchLanguage().let {
                        if (it == "vi") binding.root.context.getString(R.string.vi)
                        else binding.root.context.getString(R.string.en)
                    }
                }else if (menu.viewType == TYPE_RADIO){
                    val radioButton: RadioButton = binding.layoutMode.findViewById(DEFAULT_ID)
                    sessionManager.fetchLanguage().let {
                        if (it == null || it == ""){
                            if (menu.id == "en") radioButton.isChecked = true
                        }else radioButton.isChecked = menu.id == it
                    }
                    radioButton.setOnClickListener{
                        callBack.onItemMenu1ClickListenner(menu)
                    }
                }

                binding.layoutMode.setOnClickListener{
                    callBack.onItemMenu1ClickListenner(menu)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount(): Int {
        if (list != null) return list!!.size
        return 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list != null){
            holder.onBind(list!![position])
        }
    }

    @SuppressLint("NewApi")
    fun handleView(type: Int?, binding: ItemMenuBinding){

        val layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            this.addRule(RelativeLayout.ALIGN_PARENT_RIGHT) // Đặt quy tắc để đặt SwitchCompat ở phía bên phải
            this.addRule(RelativeLayout.CENTER_VERTICAL) // Đặt quy tắc để căn giữa theo chiều dọc
        }

        if (type == TYPE_SWITCH){
            val switchCompat: SwitchCompat = SwitchCompat(binding.layoutMode.context).apply {
                this.id = DEFAULT_ID
                this.layoutParams = layoutParams
                this.setPadding(0,0,30,0)
            }
            binding.layoutMode.addView(switchCompat)
        }else if (type == TYPE_TEXT){
            val textView: TextView = TextView(binding.layoutMode.context).apply {
                this.id = DEFAULT_ID
                this.setPadding(0, 0, 80, 0)
//                this.setTextColor(binding.layoutMode.context.getColor(R.color.text_title1))
                this.layoutParams = layoutParams
            }
            binding.layoutMode.addView(textView)
        }else if (type == TYPE_RADIO){
            val radioButton: RadioButton = RadioButton(binding.layoutMode.context).apply {
                this.id = DEFAULT_ID
                this.setPadding(0, 0, 80, 0)
                this.layoutParams = layoutParams
            }
            binding.layoutMode.addView(radioButton)
        }
    }

}