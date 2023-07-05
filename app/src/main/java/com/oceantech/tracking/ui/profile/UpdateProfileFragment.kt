package com.oceantech.tracking.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.databinding.FragmentUpdateProfileBinding

class UpdateProfileFragment : TrackingBaseFragment<FragmentUpdateProfileBinding>() {
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUpdateProfileBinding = FragmentUpdateProfileBinding.inflate(inflater,container,false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}