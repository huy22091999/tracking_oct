package com.oceantech.tracking.ui.tracking

import android.app.AlertDialog
import android.content.DialogInterface
import android.location.GnssAntennaInfo.Listener
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getDrawable
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.google.android.material.textview.MaterialTextView
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.databinding.FragmentTrackingBinding
import com.oceantech.tracking.ui.home.HomeViewAction
import com.oceantech.tracking.ui.home.HomeViewModel

class TrackingFragment : TrackingBaseFragment<FragmentTrackingBinding>() {
    private val viewModel:HomeViewModel by activityViewModel()
    private lateinit var trackingAdapter:TrackingAdapter
    private lateinit var trackings:List<Tracking>
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTrackingBinding = FragmentTrackingBinding.inflate(inflater, container, false)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trackings = listOf()
        trackingAdapter = TrackingAdapter(trackings,requireContext(), showMenu)
        views.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trackingAdapter
        }

        viewModel.trackings.observe(viewLifecycleOwner){
            trackingAdapter = TrackingAdapter(it,requireContext(), showMenu)
            views.recyclerView.adapter = trackingAdapter
        }
        viewModel.handleAllTracking()
    }

    private val showMenu:(View,Tracking) -> Unit = {view,tracking ->
        showMenu(view,tracking)
    }

    private fun showMenu(v: View, tracking: Tracking) {
        val inflater = requireActivity().getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_tracking, null)
        val popup = PopupWindow(
            view,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        popup.elevation = 20F
        popup.setBackgroundDrawable(getDrawable(requireActivity(),R.drawable.backgound_box))
        popup.showAsDropDown(v, 5, -5, Gravity.CENTER_HORIZONTAL)

        view.findViewById<MaterialTextView>(R.id.to_track_update).setOnClickListener {
            viewModel.handleReturnUpdate(tracking.content!!, tracking.id!!)
        }

        view.findViewById<MaterialTextView>(R.id.to_track_delete).setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.confirm)
                .setMessage(R.string.confirm_delete)
                .setNegativeButton(R.string.no, null)
                .setPositiveButton(R.string.yes, DialogInterface.OnClickListener { dialog, which ->
                    deleteTracking(tracking)
                })
                .show()
        }
    }
    private fun deleteTracking(tracking: Tracking) {
        viewModel.handle(HomeViewAction.DeleteTracking(tracking.id!!))
    }

    override fun invalidate():Unit = withState(viewModel){
        when(it.allTracking){
            is Success -> {
                dismissLoadingDialog()
            }
            is Loading -> {
                showLoadingDialog()
            }
            is Fail -> {
                dismissLoadingDialog()
            }
        }
        when(it.asyncDeleteTracking){
            is Success -> {
                it.asyncDeleteTracking.invoke().let {
                    Toast.makeText(requireContext(), getString(R.string.delete_success), Toast.LENGTH_SHORT).show()
                    Log.i("state of deleted: ", "success delete tracking id ${it.id}")
                }
                dismissLoadingDialog()
            }
            is Loading -> {
                viewModel.handleAllTracking()
                showLoadingDialog()
            }
            is Fail -> {
                dismissLoadingDialog()
            }
        }
    }
}