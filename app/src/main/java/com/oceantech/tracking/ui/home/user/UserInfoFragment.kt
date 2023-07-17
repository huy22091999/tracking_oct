package com.oceantech.tracking.ui.home.user

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.databinding.FragmentUserInfoBinding
import com.oceantech.tracking.ui.home.HomeFragment
import com.oceantech.tracking.ui.home.HomeViewAction
import com.oceantech.tracking.ui.home.HomeViewModel
import com.oceantech.tracking.utils.checkError
import com.oceantech.tracking.utils.handleBackPressedEvent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
@SuppressLint("LogNotTimber")
class UserInfoFragment @Inject constructor(): TrackingBaseFragment<FragmentUserInfoBinding>() {

    private val homeViewModel: HomeViewModel by activityViewModel()

    companion object{
        const val UPDATE_ID = "update_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            homeViewModel.handle(HomeViewAction.GetBlockUser(it.getInt(UPDATE_ID, 0)))
        }

    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUserInfoBinding = FragmentUserInfoBinding.inflate(layoutInflater)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("info", "onViewCreated")
        handleBackPressedEvent(findNavController()){}
        views.edtProfile.setOnClickListener {
            val bundle = bundleOf(UPDATE_ID to views.user?.id)
            findNavController().navigate(R.id.modifyUserFragment, bundle)
        }
    }

    override fun invalidate(): Unit = withState(homeViewModel) {
        when(val blockUser = it.blockUser){
            is Success -> {
                views.user = blockUser.invoke()
            }
            is Fail -> {
                blockUser.error.message?.let { it1 -> checkError(it1) }
            }
            else -> {}
        }
    }


}