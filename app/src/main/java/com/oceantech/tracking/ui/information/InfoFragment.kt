package com.oceantech.tracking.ui.information

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.databinding.FragmentInfoBinding

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint

class InfoFragment : TrackingBaseFragment<FragmentInfoBinding>() {

    private val mInfoViewModel: InfoViewModel by activityViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mInfoViewModel.handle(InfoViewAction.GetConfigApp)
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentInfoBinding{
        return FragmentInfoBinding.inflate(inflater, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun invalidate(): Unit = withState(mInfoViewModel){
        when(it.config){
            is Success -> {
                it.config.invoke()?.let {config ->
                    views.txtVersion.text = "Version: ${config.versionName}"
                }
            }
            is Fail -> {
                Log.i("Public", it.config.error.toString())
            }

            else -> {}
        }
    }



}