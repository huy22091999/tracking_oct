package com.oceantech.tracking.ui.tracking

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.FragmentAddTrackingBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone


class AddTrackingFragment : BottomSheetDialogFragment() {
    private lateinit var _binding: FragmentAddTrackingBinding
    val binding get() = _binding
    val args: AddTrackingFragmentArgs by navArgs()
    lateinit var mUser: User

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding=FragmentAddTrackingBinding.inflate(inflater,container,false)
        val view =binding.root
        mUser=args.userArg
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSave.setOnClickListener {
            submitForm();
        }
    }

    private fun submitForm() {
        val content: String = binding.inputTracking.text.toString()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val tracking = Tracking(content,dateFormat.format(Calendar.getInstance().time), 0, mUser)
        val bundle = Bundle().apply {
            putSerializable("key_tracking", tracking)
        }
        parentFragmentManager.setFragmentResult("requestKey", bundle)
        dismiss()
    }

}