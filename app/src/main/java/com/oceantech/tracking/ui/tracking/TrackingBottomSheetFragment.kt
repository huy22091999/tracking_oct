package com.oceantech.tracking.ui.tracking

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.oceantech.tracking.R
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.FragmentBottomsheetTrackingBinding
import com.oceantech.tracking.ui.profile.InfoActivity
import com.oceantech.tracking.utils.getListSuggestTracking
import com.oceantech.tracking.utils.startActivityAnim


class TrackingBottomSheetFragment(private var callBack: (message: String) -> Unit,
                                  private var tracking: Tracking?,
                                  private var curentUser: User?
) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomsheetTrackingBinding
    private lateinit var bottomBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var listSuggest: ArrayList<String>

    companion object{
        fun newInstance(tracking: Tracking?, curentUser: User?, callBack: (message: String) -> Unit): TrackingBottomSheetFragment
        = TrackingBottomSheetFragment(callBack, tracking, curentUser)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomsheetTrackingBinding.inflate(inflater, container, false)

        Glide.with(requireActivity()).load("curentUser.link!!").placeholder(R.drawable.baseline_person_24).into(binding.imgAvt)
        binding.tvName.text = curentUser?.displayName ?: "name"

        if (tracking == null){
            binding.tvTitle.text = requireActivity().getString(R.string.create_tracking)
            binding.btnAccept.text = requireActivity().getString(R.string.post)
        }else{
            binding.tvTitle.text = requireActivity().getString(R.string.edit_tracking)
            binding.btnAccept.text = requireActivity().getString(R.string.save)
            binding.edtContent.setText(tracking!!.content)
        }

        listSuggest = getListSuggestTracking() as ArrayList<String>
        binding.listview.apply {
            this.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, listSuggest)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBottomSheet()
        setupBottomshetBehavior()
        lisstenClickUI()
    }

    private fun setupBottomSheet() {
        val bottomSheetDialog = dialog as BottomSheetDialog
        val behavior = bottomSheetDialog.behavior
        behavior.isDraggable = false
        behavior.state = STATE_EXPANDED
    }

    private fun lisstenClickUI() {
        binding.btnAccept.setOnClickListener{
            callBack(binding.edtContent.text.toString())
            this.dismiss()
        }

        binding.imgBack.setOnClickListener{
            this.dismiss()
        }

        binding.imgUp.setOnClickListener{
            bottomBehavior.state = STATE_EXPANDED
        }

        binding.listview.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            binding.edtContent.setText(listSuggest[position])
        }

        binding.tvName.setOnClickListener(onClickStartActivity)
        binding.cvAvt.setOnClickListener(onClickStartActivity)
    }

    var onClickStartActivity = OnClickListener{
        requireActivity().startActivityAnim(Intent(requireActivity(), InfoActivity::class.java))
    }

    private fun setupBottomshetBehavior() {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)

        bottomBehavior = BottomSheetBehavior.from(binding.bottomsheet).apply {
                this.state = STATE_EXPANDED
                this.peekHeight = (displayMetrics.heightPixels * 0.1).toInt()   // 10 % màn hình đó
                isHideable = false
            }


        bottomBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    STATE_EXPANDED ->{
                        binding.imgUp.setImageResource(R.drawable.baseline_horizontal_rule_24)
                    }
                    STATE_COLLAPSED -> {
                        binding.imgUp.setImageResource(R.drawable.baseline_keyboard_arrow_up_24)
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })
    }


}