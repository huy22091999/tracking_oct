package com.oceantech.tracking.ui.tracking

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.GnssAntennaInfo.Listener
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.getDrawable
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.databinding.FragmentTrackingBinding
import com.oceantech.tracking.ui.home.HomeViewAction
import com.oceantech.tracking.ui.home.HomeViewEvent
import com.oceantech.tracking.ui.home.HomeViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class TrackingFragment @Inject constructor() : TrackingBaseFragment<FragmentTrackingBinding>() {
    private val viewModel: HomeViewModel by activityViewModel()
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

        viewModel.observeViewEvents {
            handleEvent(it)
        }
    }
    private fun handleEvent(it: HomeViewEvent) {
        when(it){
            is HomeViewEvent.ResetLanguege -> {

            }
        }
    }
    override fun onResume() {
        super.onResume()
        viewModel.handleAllTracking()
    }

    private val showMenu:(View,Tracking) -> Unit = { _, tracking ->
        showBottomDialog(tracking)
    }

    private fun showBottomDialog(tracking: Tracking){
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottom_sheet_tracking_layout)

        val updateSubmit:MaterialButton = dialog.findViewById(R.id.option_update)
        val deleteSubmit:MaterialButton = dialog.findViewById(R.id.option_delete)
        val dismissSubmit:CardView = dialog.findViewById(R.id.option_dismiss)

        updateSubmit.setOnClickListener {
            viewModel.handleReturnUpdate(tracking.content!!, tracking.id!!)
            dialog.dismiss()
        }

        dismissSubmit.setOnClickListener {
            dialog.dismiss()
        }

        deleteSubmit.setOnClickListener {
            dialog.dismiss()
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.confirm)
                .setMessage(R.string.confirm_delete)
                .setNegativeButton(R.string.no, null)
                .setPositiveButton(R.string.yes) { _, _ ->
                    deleteTracking(tracking)
                }
                .show()
        }

        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM);
    }

    private fun deleteTracking(tracking: Tracking) {
        viewModel.handle(HomeViewAction.DeleteTracking(tracking.id!!))
    }

    override fun invalidate():Unit = withState(viewModel){
        when(it.allTracking){
            is Success -> {
                it.allTracking.invoke()?.let { trackings ->
                    views.recyclerView.apply {
                        adapter = TrackingAdapter(trackings,requireContext(), showMenu)
                    }
                }
                views.imageView.setOnClickListener {
                    viewModel.handleReturnAddTracking()
                }
            }
        }
        when(it.asyncDeleteTracking){
            is Success -> {
                it.asyncDeleteTracking.invoke().let {
                    Toast.makeText(requireContext(), getString(R.string.delete_success), Toast.LENGTH_SHORT).show()
                }
                viewModel.handleRemoveStateOfDelete()
            }
            is Loading -> {
                viewModel.handleAllTracking()
            }
            is Fail -> {
            }
        }
    }
}