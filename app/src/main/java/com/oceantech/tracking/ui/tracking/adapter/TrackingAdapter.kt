package com.oceantech.tracking.ui.tracking.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.oceantech.tracking.R
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.databinding.NoTrackingsBinding
import com.oceantech.tracking.databinding.TrackingItemBinding
import com.oceantech.tracking.ui.tracking.TrackingViewAction
import com.oceantech.tracking.ui.tracking.TrackingViewModel
import com.oceantech.tracking.utils.TrackingBaseAdapter
import java.lang.Exception

class TrackingAdapter(
    private val deleteTracking: (Int) -> Unit,
    private val updateTracking: (Tracking, Int) -> Unit
) : TrackingBaseAdapter<Tracking>() {

    override fun getBinding(parent: ViewGroup, viewType: Int): ViewBinding {
        return TrackingItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    @SuppressLint("InflateParams")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tracking = list[position]
        val binding = holder.binding as TrackingItemBinding
        binding.tracking = tracking

        binding.imgMore.setOnClickListener {
            val viewSwipe = LayoutInflater.from(holder.binding.root.context)
                .inflate(R.layout.swipe_menu_layout, null)
            BottomSheetDialog(holder.binding.root.context).also { sheet ->
                sheet.setContentView(viewSwipe.also { view ->
                    view.findViewById<LinearLayout>(R.id.deleteLayout)
                        .setOnClickListener {
                            deleteTracking(tracking.id!!)
                            sheet.dismiss()
                        }

                    view.findViewById<LinearLayout>(R.id.editLayout).setOnClickListener {
                        val view = LayoutInflater.from(holder.binding.root.context)
                            .inflate(R.layout.update_tracking, null)
                        val edtContent = view.findViewById<EditText>(R.id.edtContent)
                        edtContent.setText(tracking.content)

                        AlertDialog.Builder(holder.binding.root.context).apply {
                            setIcon(R.drawable.add_tracking_icon)
                            setTitle(context.getString(R.string.update_tracking))
                            setView(view)
                            setPositiveButton(context.getString(R.string.update)) { dialog, which ->
                                val newTracking =
                                    tracking.copy(content = edtContent.text.toString())
                                updateTracking(newTracking, newTracking.id!!)
                                sheet.dismiss()
                            }
                            setNegativeButton(context.getString(R.string.Cancel), null)
                        }.create().show()
                    }

                })
                sheet.setCancelable(true)
                sheet.show()
            }
        }

    }

    companion object {
        private const val NO_TRACKING_STATE = 0
        private const val EXIST_TRACKING_STATE = 1
    }

}