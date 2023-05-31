package com.oceantech.tracking.ui.security


import android.view.LayoutInflater
import android.view.ViewGroup
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.databinding.FragmentResetPasswordBinding


class ResetPasswordFragment :TrackingBaseFragment<FragmentResetPasswordBinding>() {
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentResetPasswordBinding {
        return FragmentResetPasswordBinding.inflate(inflater,container,false)
    }

}