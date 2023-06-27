package com.oceantech.tracking.ui.tracking.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.oceantech.tracking.R
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.databinding.TrackingItemBinding
import com.oceantech.tracking.ui.tracking.TrackingViewAction
import com.oceantech.tracking.ui.tracking.TrackingViewModel

class TrackingAdapter(
    private val deleteTracking: (Int) -> Unit,
    private val updateTracking: (Tracking, Int) -> Unit
): RecyclerView.Adapter<TrackingAdapter.TrackingViewHolder>() {

    private var listTracking: List<Tracking> = emptyList()

    class TrackingViewHolder(private val _binding: TrackingItemBinding): RecyclerView.ViewHolder(_binding.root){
        val binding: TrackingItemBinding
            get() = _binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackingViewHolder {
        return TrackingViewHolder(
            TrackingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return listTracking.size
    }

    fun setListTracking(listTracking: List<Tracking>){
        this.listTracking = listTracking
    }
    @SuppressLint("InflateParams")
    override fun onBindViewHolder(holder: TrackingViewHolder, position: Int) {
        val tracking = listTracking[position]
        holder.binding.tracking = tracking

        holder.binding.imgDelTracking.setOnClickListener {
            deleteTracking(tracking.id!!)
        }
        holder.binding.root.setOnClickListener {
            val view = LayoutInflater.from(holder.binding.root.context).inflate(R.layout.update_tracking, null)
            val edtContent = view.findViewById<EditText>(R.id.edtContent)
            edtContent.setText(tracking.content)
            val alertDialog = AlertDialog.Builder(holder.binding.root.context).apply {
                setIcon(R.drawable.add_tracking_icon)
                setTitle(context.getString(R.string.update_tracking))
                setView(view)
                setPositiveButton(context.getString(R.string.update)){ dialog, which ->
                    val newTracking = tracking.copy(content = edtContent.text.toString() )
                    updateTracking(newTracking, newTracking.id!!)
                }
                setNegativeButton(context.getString(R.string.Cancel), null)
            }.create().show()
        }
    }


}