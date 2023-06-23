package com.oceantech.tracking.ui.public_config

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.ConfigApp
import com.oceantech.tracking.databinding.FragmentPublicBinding


class PublicFragment : TrackingBaseFragment<FragmentPublicBinding>() {

    private val publicViewModel: PublicViewModel by activityViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        publicViewModel.handle(PublicViewAction.GetConfigApp)
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPublicBinding {
        return FragmentPublicBinding.inflate(inflater, container, false)
    }

    override fun invalidate(): Unit = withState(publicViewModel){
        when(it.config){
            is Success -> {
                it.config.invoke()?.let {config ->
                    views.txtVersion.text = "Version: ${config.versionName}"
                }
            }
            is Fail -> {
                Log.i("Public", (it.config as Fail<ConfigApp>).error.toString())
            }
        }
    }



}