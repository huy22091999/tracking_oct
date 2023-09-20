package com.oceantech.tracking.ui.tracking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.databinding.FragmentAddTrackingBinding
import com.oceantech.tracking.utils.StringUltis.dateIso8601Format2
import com.oceantech.tracking.utils.convertDateToStringFormat
import java.util.Date
//done
class AddTrackingFragment : BottomSheetDialogFragment() {

    private lateinit var _binding: FragmentAddTrackingBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddTrackingBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheetBehavior= BottomSheetBehavior.from(view.parent as View )
        bottomSheetBehavior.state= BottomSheetBehavior.STATE_EXPANDED
        listenEvent()
    }

    private fun listenEvent() {
        binding.btnSave.setOnClickListener {
            submitForm();
        }
        binding.btnDismiss.setOnClickListener {
            dismiss()
        }
    }
    private fun submitForm() {
        val content: String = binding.inputTracking.text.toString()
        val dateTracking = Date().convertDateToStringFormat(dateIso8601Format2)
        val tracking = Tracking(content,dateTracking, 0, null)
        val bundle = Bundle().apply {
            putSerializable("key_tracking", tracking)
        }
        parentFragmentManager.setFragmentResult("requestKey", bundle)
        dismiss()
    }

}