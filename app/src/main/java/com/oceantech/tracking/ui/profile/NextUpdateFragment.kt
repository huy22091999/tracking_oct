package com.oceantech.tracking.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.databinding.FragmentNextSigninBinding

class NextUpdateFragment : TrackingBaseFragment<FragmentNextSigninBinding>(){
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNextSigninBinding = FragmentNextSigninBinding.inflate(inflater,container, false)

}