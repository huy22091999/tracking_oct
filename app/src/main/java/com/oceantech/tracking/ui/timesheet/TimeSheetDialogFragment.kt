package com.oceantech.tracking.ui.timesheet

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.oceantech.tracking.R
import com.oceantech.tracking.data.model.TimeSheet
import com.oceantech.tracking.databinding.DialogTimesheetBinding
import com.oceantech.tracking.ui.profile.InfoAdapter
import com.oceantech.tracking.utils.StringUltis
import com.oceantech.tracking.utils.convertToStringFormat

class TimeSheetDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding : DialogTimesheetBinding

    companion object{
        private const val KEY_ARGUMENTS = "key_timesheet"
        private var timeSheetDialogFragment : TimeSheetDialogFragment? = null

        fun getInstance(timeSheet: TimeSheet) : BottomSheetDialogFragment{
            if (timeSheetDialogFragment == null ) timeSheetDialogFragment = TimeSheetDialogFragment()

            var bundle = Bundle()
            bundle.putSerializable(KEY_ARGUMENTS, timeSheet)
            timeSheetDialogFragment!!.arguments = bundle

            return timeSheetDialogFragment!!
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set style border radius
        setStyle(STYLE_NORMAL, R.style. AppBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogTimesheetBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("LogNotTimber")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        var dialog = Dialog(requireContext())
//        var bindingDialog = DialogTimesheetBinding.inflate(dialog.layoutInflater)
//        dialog.setContentView(bindingDialog.root)
//
//        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
//        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dialog.window?.setGravity(Gravity.BOTTOM);
//        dialog.show()

        val timeSheet : TimeSheet? = arguments?.getSerializable(KEY_ARGUMENTS) as TimeSheet?

        if (timeSheet != null){
            if (timeSheet.offline == true) binding.tvDate.setTextColor(Color.GREEN)
            else binding.tvDate.setTextColor(Color.RED)
            Log.e("TAG", "onViewCreated: ${timeSheet}" )
            binding.tvDate.text = timeSheet.dateAttendance!!.convertToStringFormat(StringUltis.dateIso8601Format, StringUltis.dateDayTimeFormat)
            binding.tvMessage.text = timeSheet.message ?: getString(com.oceantech.tracking.R.string.no_message)
        }
    }

}