package com.oceantech.tracking.ui.menu

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.bumptech.glide.Glide
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.core.TrackingClickItem
import com.oceantech.tracking.data.model.Menu
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.databinding.FragmentMenuBinding
import com.oceantech.tracking.ui.home.HomeViewModel
import com.oceantech.tracking.ui.profile.InfoActivity
import com.oceantech.tracking.ui.profile.InfoViewModel
import com.oceantech.tracking.ui.profile.MenuBottomSheetDialogFragment
import com.oceantech.tracking.utils.getMenuLanguage
import com.oceantech.tracking.utils.getMenus
import com.oceantech.tracking.utils.startActivityAnim
import javax.inject.Inject

class MenuFragment @Inject constructor() : TrackingBaseFragment<FragmentMenuBinding>(){

    private val infoViewModel: InfoViewModel by activityViewModel()
    private val homeViewMode: HomeViewModel by activityViewModel()

    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: MenuAdapter

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMenuBinding {
        return FragmentMenuBinding.inflate(inflater, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext().applicationContext)

        setupRcv()

        lisstenCLickUI()

        homeViewMode.subscribe(requireActivity()){
            when(it.userCurrent){
                is Success ->{
                    it.userCurrent.invoke().let {user ->
                        Glide.with(requireActivity()).load("user.link").placeholder(R.drawable.baseline_person_24).into(views.imgAvt)
                        views.tvName.text = user.displayName
                    }
                }
                else -> {}
            }
        }
    }

    private fun setupRcv() {
        adapter = MenuAdapter(sessionManager, object: TrackingClickItem(){
            override fun onItemMenu1ClickListenner(menu1: Menu) {
                super.onItemMenu1ClickListenner(menu1)
                if(menu1.id == "1"){
                    showBottomSheetLanguage()
                }
            }

            override fun onSwitchMenu1ClickListenner(isBoolean: Boolean) {
                super.onSwitchMenu1ClickListenner(isBoolean)
                homeViewMode.returnEventSwitchMode(isBoolean)
            }
        })
        views.rcvMenu.adapter = adapter
        views.rcvMenu.layoutManager = LinearLayoutManager(requireContext())
        views.rcvMenu.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        adapter.setData(getMenus(requireContext()))
    }

    private fun showBottomSheetLanguage() {
        MenuBottomSheetDialogFragment.getInstanceMenu1(
            true,
            sessionManager,
            getMenuLanguage(requireContext()),
            object : TrackingClickItem(){
                override fun onItemMenu1ClickListenner(menu1: Menu) {
                    super.onItemMenu1ClickListenner(menu1)
                    homeViewMode.returnEventChangeLanguage(menu1.id)
                }
            }
        ).show(requireActivity().supportFragmentManager, MenuBottomSheetDialogFragment.TAG)
    }

    private fun lisstenCLickUI() {
        views.cvHeader.setOnClickListener{
            requireActivity().startActivityAnim(Intent(requireActivity(), InfoActivity::class.java))
        }


        views.cvLogout.setOnClickListener{
            homeViewMode.returnEventLogout()
        }
    }


}