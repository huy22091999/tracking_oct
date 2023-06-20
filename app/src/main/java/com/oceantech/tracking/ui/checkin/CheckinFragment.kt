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
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.databinding.FragmentCheckinBinding
import java.util.*
@SuppressLint("NotifyDataSetChanged")
class CheckinFragment : TrackingBaseFragment<FragmentCheckinBinding>(){
    private lateinit var adapter: CheckinAdapter

    private lateinit var sessionManager: SessionManager

    companion object {
        private const val IP = 0
        private const val CHECK_IN = 1
        private const val GET = 2
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
        adapter = CheckinAdapter()
        views.btnCheckin.setOnClickListener {
            state = IP
            viewModel.handle(CheckinViewAction.GetIp)
        }
        views.rcvTimeSheet.layoutManager = GridLayoutManager(requireContext(),3)
        views.rcvTimeSheet.adapter = adapter
    }
    override fun invalidate()  : Unit = withState(viewModel){
        when(state){
            CHECK_IN ->  handleCheckin(it)
            GET -> handleGetTimeSheet(it)
            IP -> handleGetIp(it)
        }
    }

    private fun handleGetTimeSheet(it: CheckinViewState) {
        when(it.asyncTimeSheet){
            is Success -> {
                it.asyncTimeSheet.invoke()?.let { it1 -> adapter.setData(it1) }
                adapter.notifyDataSetChanged()
            }
            is Fail -> {Toast.makeText(requireContext(),"get timesheet fail!",Toast.LENGTH_SHORT).show()}
        }
    }

    private fun handleCheckin(it: CheckinViewState) {
        when(it.asyncCheckin){
            is Success -> {
                val timeSheet = it.asyncCheckin.invoke()
                Toast.makeText(requireContext(),"${timeSheet?.message}",Toast.LENGTH_SHORT).show()
                state = GET
            }
            is Fail -> Toast.makeText(requireContext(),"Checkin Fail!",Toast.LENGTH_SHORT).show()
        }
    }
    private fun handleGetIp(it : CheckinViewState){
        when(it.asyncIp){
            is Success -> {
                val ip = it.asyncIp.invoke()
                if (ip != null) {
                    state = CHECK_IN
                    viewModel.handle(CheckinViewAction.Checkin(ip))
                }
            }
            is Fail -> Toast.makeText(requireContext(),"Get ip fail",Toast.LENGTH_SHORT).show()
        }
    }
}