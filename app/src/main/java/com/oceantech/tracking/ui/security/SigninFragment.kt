package com.oceantech.tracking.ui.security

import android.view.LayoutInflater
import android.view.ViewGroup
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.databinding.FragmentSigninBinding


class SigninFragment : TrackingBaseFragment<FragmentSigninBinding>() {
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSigninBinding {
        return FragmentSigninBinding.inflate(inflater,container,false)
    }

}