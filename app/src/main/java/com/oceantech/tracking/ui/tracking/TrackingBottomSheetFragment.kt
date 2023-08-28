package com.oceantech.tracking.ui.tracking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.oceantech.tracking.databinding.FragmentBottomsheetTrackingBinding

class TrackingBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBottomsheetTrackingBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomsheetTrackingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.hello
//        mViewModel.test
    }


}