package com.oceantech.tracking.ui.trackings

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.databinding.BottomSheetEditBinding
import com.oceantech.tracking.databinding.BottomSheetOptionBinding
import com.oceantech.tracking.databinding.FragmentTrackingListBinding
import javax.inject.Inject

class TrackingListFragment @Inject constructor() :
    TrackingBaseFragment<FragmentTrackingListBinding>(),OnClickTracking {
    private val viewModel: TrackingListViewModel by activityViewModel()
    private lateinit var adapter: TrackingAdapter
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTrackingListBinding {
        return FragmentTrackingListBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.handle(TrackingViewAction.GetAllTracking)
        adapter = TrackingAdapter(this)
        views.rcvTracking.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        views.rcvTracking.adapter = adapter
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun invalidate(): Unit = withState(viewModel) {
        when (it.asyncListTracking) {
            is Success -> {
               it.asyncListTracking.invoke()?.let { it1 ->
                    adapter.setData(it1)
                    adapter.notifyDataSetChanged()
                }
            }
            is Fail -> Log.e("Load tracking", "error")
        }
        when(it.asyncDelete){
            is Success -> Toast.makeText(requireContext(),"Delete success",Toast.LENGTH_LONG).show()
            is Fail -> Toast.makeText(requireContext(),"Delete Fail",Toast.LENGTH_LONG).show()
        }
        when(it.asyncUpdate){
            is Success -> Toast.makeText(requireContext(),"Update success",Toast.LENGTH_LONG).show()
            is Fail -> Toast.makeText(requireContext(),"Update fail",Toast.LENGTH_LONG).show()
        }
    }

    override fun onClick(tracking: Tracking) {
        val bDialog = BottomSheetDialog(requireContext())
        val binding = BottomSheetOptionBinding.inflate(LayoutInflater.from(requireContext()))
        bDialog.setContentView(binding.root)
        binding.tvRemove.setOnClickListener {
            viewModel.handle(TrackingViewAction.Delete(tracking))
        }
        binding.tvEdit.setOnClickListener { openEditDialog(bDialog,tracking) }
        bDialog.show()
    }

    private fun openEditDialog(bDialog: BottomSheetDialog, tracking: Tracking) {
        val dialog = BottomSheetDialog(requireContext())
        val binding = BottomSheetEditBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.setContentView(binding.root)
        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
            bDialog.dismiss()
        }
        binding.btnSave.setOnClickListener {
            saveTracking(dialog,bDialog,binding,tracking)
        }
        dialog.show()
    }

    private fun saveTracking(
        dialog: BottomSheetDialog,
        bDialog: BottomSheetDialog,
        binding: BottomSheetEditBinding,
        tracking: Tracking
    ) {
        val newContent = binding.edtContent.text.toString().trim()
        if (newContent.isNotEmpty()){
            val newTracking = tracking.copy(content = newContent)
            viewModel.repository.updateTracking(newTracking)
            dialog.dismiss()
            bDialog.dismiss()
        }
    }
}