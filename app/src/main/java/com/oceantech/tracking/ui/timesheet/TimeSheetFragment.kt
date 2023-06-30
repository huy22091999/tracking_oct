package com.oceantech.tracking.ui.timesheet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.TimeSheet
import com.oceantech.tracking.databinding.FragmentTimeSheetBinding
import com.oceantech.tracking.ui.home.HomeViewAction
import com.oceantech.tracking.ui.home.HomeViewEvent
import com.oceantech.tracking.ui.home.HomeViewModel
import java.net.NetworkInterface

class TimeSheetFragment : TrackingBaseFragment<FragmentTimeSheetBinding>() {
    private val viewModel:HomeViewModel by activityViewModel()
    private lateinit var timeSheets:List<TimeSheet>
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTimeSheetBinding = FragmentTimeSheetBinding.inflate(inflater,container,false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        timeSheets = listOf()

        views.gridView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = TimeSheetAdapter(timeSheets)
        }

        views.checkInSubmit.setOnClickListener {
            checkIn()
        }
        viewModel.observeViewEvents {
            handleEvent(it)
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun handleEvent(event: HomeViewEvent){
        when(event){
            is HomeViewEvent.ResetLanguege ->{

            }
        }
    }

    private fun checkIn(){
        viewModel.handle(HomeViewAction.GetCheckIn(getIPAddress()))
    }

    private fun getIPAddress(): String {
        val interfaces = NetworkInterface.getNetworkInterfaces()
        while (interfaces.hasMoreElements()) {
            val networkInterface = interfaces.nextElement()
            val addresses = networkInterface.inetAddresses
            while (addresses.hasMoreElements()) {
                val address = addresses.nextElement()
                if (!address.isLoopbackAddress && address.hostAddress.contains(":").not()) {
                    return address.hostAddress
                }
            }
        }
        return ""
    }

    override fun invalidate():Unit = withState(viewModel){
        when(it.timeSheets){
            is Success -> {
                it.timeSheets.invoke()?.let { timeSheets ->
                    views.gridView.apply {
                        adapter = TimeSheetAdapter(timeSheets)
                    }
                }
            }
            is Loading -> {
                Log.e("state of time sheets:", "loading")
            }
            is Fail -> {
                Log.e("state of time sheets:", "fail")
            }
        }
        when(it.checkIn){
            is Success -> {
                it.checkIn?.invoke().let { checkIn ->
                    if(checkIn.message.isNullOrEmpty()){
                        Toast.makeText(requireContext(), getString(R.string.tracking_success), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.checked_in), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            is Fail -> {
            }
            is Loading -> {
                viewModel.handleTimeSheets()
                Toast.makeText(requireContext(), getString(R.string.checked_in), Toast.LENGTH_SHORT).show()
            }
        }
    }
}