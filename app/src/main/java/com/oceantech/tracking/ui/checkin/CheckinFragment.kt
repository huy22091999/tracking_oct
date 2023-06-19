package com.oceantech.tracking.ui.checkin

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.mvrx.*
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.TimeSheet
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.databinding.FragmentCheckinBinding
import com.oceantech.tracking.utils.getIPAddress
import java.text.SimpleDateFormat
import java.util.*

class CheckinFragment : TrackingBaseFragment<FragmentCheckinBinding>(){
    private lateinit var adapter: CheckinAdapter

    private lateinit var sessionManager: SessionManager

    companion object {
        private const val CHECK_IN = 0
        private const val GET = 1
    }
    private val viewModel: CheckinViewModel by activityViewModel()
    private var state = 0

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCheckinBinding {
        return FragmentCheckinBinding.inflate(LayoutInflater.from(context),container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        state = GET
        sessionManager = SessionManager(requireContext())
        viewModel.handle(CheckinViewAction.GetTimeSheet)
        val ip = getIPAddress()
        adapter = CheckinAdapter()
        views.btnCheckin.setOnClickListener {
            validateCheckin(ip)
        }
        views.rcvTimeSheet.layoutManager = GridLayoutManager(requireContext(),3)
        views.rcvTimeSheet.adapter = adapter
    }

    @SuppressLint("SimpleDateFormat")
    private fun validateCheckin(ip: String?) {
        val savedDate = sessionManager.getCheckin()
        val calendar = Calendar.getInstance().time
        val currentDate = SimpleDateFormat("dd/MM/yyyy").format(calendar)
        if (savedDate == currentDate){
            Toast.makeText(requireContext(),getString(R.string.noti_checkin),Toast.LENGTH_SHORT).show()
        }
        else {
            viewModel.handle(CheckinViewAction.Checkin(ip.toString()))
            sessionManager.saveCheckin(currentDate)
        }
    }

    override fun invalidate()  : Unit = withState(viewModel){
        when(state){
            CHECK_IN ->  handleCheckin(it.asyncCheckin)
            GET -> handleGetTimeSheet(it.asyncTimeSheet)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun handleGetTimeSheet(asyncTimeSheet: Async<List<TimeSheet>>) {
        when(asyncTimeSheet){
            is Success -> {
                adapter.setData(asyncTimeSheet.invoke())
                adapter.notifyDataSetChanged()
            }
            is Fail -> {Toast.makeText(requireContext(),"get timesheet fail!",Toast.LENGTH_SHORT).show()}
        }
    }

    private fun handleCheckin(asyncCheckin: Async<TimeSheet>) {
        when(asyncCheckin){
            is Success -> Toast.makeText(requireContext(),"Checkin success!",Toast.LENGTH_SHORT).show()
            is Fail -> Toast.makeText(requireContext(),"Checkin Fail!",Toast.LENGTH_SHORT).show()
        }
    }
}