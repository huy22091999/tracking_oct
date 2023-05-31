package com.oceantech.tracking.ui.medical

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.oceantech.tracking.data.model.HealthOrganization
import com.oceantech.tracking.databinding.ItemHealthOrganizationBinding

class MedicalAdapter(
    val context: Context
) :
    PagingDataAdapter<HealthOrganization, MedicalAdapter.HealthOrganizationViewHolder>(COMPARATOR) {

    class HealthOrganizationViewHolder(val context: Context,val binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindHeath(healthOrganization: HealthOrganization) {
            with(binding as ItemHealthOrganizationBinding){
                nameHealth.text=healthOrganization.name
                adressHealth.text=healthOrganization.address
            }
        }
    }

    override fun onBindViewHolder(holder: HealthOrganizationViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bindHeath(it)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HealthOrganizationViewHolder {
        val itemBinding:ViewBinding=ItemHealthOrganizationBinding.inflate(LayoutInflater.from(context),parent,false)
        return HealthOrganizationViewHolder(context,itemBinding)
    }
    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<HealthOrganization>() {
            override fun areItemsTheSame(oldItem: HealthOrganization, newItem: HealthOrganization): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: HealthOrganization, newItem: HealthOrganization): Boolean =
                oldItem == newItem

        }
    }
}