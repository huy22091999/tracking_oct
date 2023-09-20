package com.oceantech.tracking.ui.profile

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingClickItem
import com.oceantech.tracking.data.model.Menu
import com.oceantech.tracking.data.model.Menu2
import com.oceantech.tracking.data.model.TimeSheet
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.databinding.DialogBottomInfoBinding
import com.oceantech.tracking.ui.menu.MenuAdapter
import com.oceantech.tracking.ui.timesheet.TimeSheetDialogFragment

class MenuBottomSheetDialogFragment constructor(
    private val isBorder: Boolean,
    private var sessionManager: SessionManager?,
    private val listMenu1: ArrayList<Menu>?,
    private val listMenu2: ArrayList<Menu2>?,
    private var callBack: TrackingClickItem
) : BottomSheetDialogFragment() {


    companion object{
        const val TAG = "MenuBottomSheetDialogFragment"
        const val STATE_HIGH = 0
        const val STATE_NOMAL = 1
        const val STATE_LOW = 2

        lateinit var adapterMenu1: MenuAdapter
        lateinit var adapterMenu2: InfoAdapter

        fun getInstanceMenu1(isBorder: Boolean, sessionManager: SessionManager, list: ArrayList<Menu>, callBack: TrackingClickItem) : BottomSheetDialogFragment
                = MenuBottomSheetDialogFragment(isBorder, sessionManager, list, null, callBack)

        fun getInstanceMenu2(isBorder: Boolean, list: ArrayList<Menu2>, callBack: TrackingClickItem) : BottomSheetDialogFragment
        = MenuBottomSheetDialogFragment(isBorder, null,null, list, callBack)

    }

    lateinit var binding: DialogBottomInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isBorder){
            setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogBottomInfoBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBottomSheet()

        if (listMenu1 != null && sessionManager != null){
            setupRcvMenu1()
        }else if (listMenu2 != null){
            setupRcvMenu2()
        }else{

        }
    }

    private fun setupRcvMenu2() {
        adapterMenu2 = InfoAdapter(object : TrackingClickItem(){
            override fun onItemMenu2ClickListenner(menu2: Menu2) {
                super.onItemMenu2ClickListenner(menu2)
                callBack.onItemMenu2ClickListenner(menu2)
                dismiss()
            }
        })
        binding.rcv.adapter = adapterMenu2
        adapterMenu2.setData(listMenu2)
        binding.rcv.layoutManager = LinearLayoutManager(requireContext())
        binding.rcv.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
    }

    private fun setupRcvMenu1() {
        adapterMenu1 = MenuAdapter(sessionManager!!, object : TrackingClickItem(){
            override fun onItemMenu1ClickListenner(menu1: Menu) {
                super.onItemMenu1ClickListenner(menu1)
                callBack.onItemMenu1ClickListenner(menu1)
                dismiss()
            }
        })
        binding.rcv.adapter = adapterMenu1
        adapterMenu1.setData(listMenu1)
        binding.rcv.layoutManager = LinearLayoutManager(requireContext())
        binding.rcv.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
    }

    private fun setupBottomSheet() {
        val bottomSheetDialog = dialog as BottomSheetDialog
        val behavior = bottomSheetDialog.behavior
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }
}