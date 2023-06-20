package com.oceantech.tracking.ui.trackings

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.databinding.BottomSheetEditBinding
import com.oceantech.tracking.databinding.BottomSheetOptionBinding
import com.oceantech.tracking.databinding.FragmentTrackingListBinding
import timber.log.Timber
import javax.inject.Inject
@SuppressLint("NotifyDataSetChanged")
class TrackingListFragment @Inject constructor() :
    TrackingBaseFragment<FragmentTrackingListBinding>(), OnClickTracking {
    private val viewModel: TrackingListViewModel by activityViewModel()
    private lateinit var adapter: TrackingAdapter
    private var state : Int = 0

    companion object {
        private const val GET_ALL = 1
        private const val DELETE = 2
        private const val UPDATE = 3
    }
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTrackingListBinding {
        return FragmentTrackingListBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.handle(TrackingViewAction.GetAllTracking)
        state = GET_ALL
        adapter = TrackingAdapter(this)
        views.rcvTracking.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        views.rcvTracking.adapter = adapter
    }

    override fun invalidate(): Unit = withState(viewModel) {
        when(state){
            GET_ALL -> handleGetAll(it)
            DELETE -> handleDelete(it)
            UPDATE -> handleUpdate(it)
        }
    }
    private fun handleGetAll(state: TrackingViewState) {
        when (val result = state.asyncListTracking) {
            is Success -> {
                result.invoke().let { list ->
                    adapter.setData(list)
                    adapter.notifyDataSetChanged()
                }
            }
            is Fail -> {
                Timber.tag("Load tracking").e("error")
            }
        }
    }
    private fun handleDelete(it: TrackingViewState) {
        when (it.asyncDelete) {
            is Success -> {
                    Toast.makeText(requireContext(), "Delete success", Toast.LENGTH_SHORT).show()
                    state = GET_ALL
            }
            is Fail -> Toast.makeText(requireContext(), "Delete Fail", Toast.LENGTH_SHORT).show()
        }
    }
    private fun handleUpdate(it : TrackingViewState){
        when (it.asyncUpdate) {
            is Success -> {
                Toast.makeText(requireContext(), "Update success", Toast.LENGTH_SHORT).show()
                state = GET_ALL
            }
            is Fail -> Toast.makeText(requireContext(), "Update fail", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick(tracking: Tracking) {
        val bDialog = BottomSheetDialog(requireContext())
        val binding = BottomSheetOptionBinding.inflate(LayoutInflater.from(requireContext()))
        bDialog.setContentView(binding.root)
        binding.tvRemove.setOnClickListener {
            state = DELETE
            viewModel.handle(TrackingViewAction.Delete(tracking))
            bDialog.dismiss()
        }
        binding.tvEdit.setOnClickListener { openEditDialog(bDialog, tracking) }
        bDialog.show()
    }

    private fun openEditDialog(bDialog: BottomSheetDialog, tracking: Tracking) {
        val dialog = BottomSheetDialog(requireContext())
        val binding = BottomSheetEditBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.setContentView(binding.root)
        binding.edtContent.setText(tracking.content)
        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
            bDialog.dismiss()
        }
        binding.btnSave.setOnClickListener {
            saveTracking(dialog, bDialog, binding, tracking)
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
        if (newContent.isNotEmpty()) {
            state = UPDATE
            val newTracking = tracking.copy(content = newContent)
            viewModel.handle(TrackingViewAction.Update(newTracking))
            dialog.dismiss()
            bDialog.dismiss()
        }
    }
}