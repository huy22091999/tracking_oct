package com.oceantech.tracking.ui.users

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.FragmentUserDetailBinding

class UserDetailFragment : TrackingBaseFragment<FragmentUserDetailBinding>() {

    val args : UserDetailFragmentArgs by navArgs()

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUserDetailBinding {
        return FragmentUserDetailBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = args.ReviveUser

        views.tvName.text = user.displayName
        views.tvEmail.text = user.email
        views.tvEducation.text = user.university

        Glide.with(requireContext()).load("user.link").placeholder(R.drawable.ic_person).into(views.imgAvt)
    }



}