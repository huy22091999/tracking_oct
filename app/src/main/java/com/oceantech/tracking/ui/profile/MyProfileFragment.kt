package com.oceantech.tracking.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.FragmentMyProfileBinding
import com.oceantech.tracking.ui.home.HomeViewAction
import com.oceantech.tracking.ui.home.HomeViewModel

class MyProfileFragment : TrackingBaseFragment<FragmentMyProfileBinding>() {
    private val viewModel:HomeViewModel by activityViewModel()
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMyProfileBinding = FragmentMyProfileBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.handle(HomeViewAction.GetCurrentUser)

    }
    private fun setData(user: User){
        views.apply {
            email.text = user.email
            displayName.text = user.displayName
            username.text = user.username
            lastname.text = user.lastName
            firstName.text = user.firstName
            gender.text = user.gender
            university.text = user.university
            year.text = user.year.toString()

            views.updateButton.setOnClickListener {
                viewModel.handleReturnUpdateInfo(user)
            }
        }
    }

    override fun invalidate():Unit = withState(viewModel){
        when(it.userCurrent){
            is Success -> {
                it.userCurrent.invoke()?.let { user ->
                    views.apply {
                        setData(user)
                    }
                    views.progressBar.visibility = View.GONE
                    views.container.visibility = View.VISIBLE
                }
            }
            is Loading -> {
                views.progressBar.visibility = View.VISIBLE
                views.container.visibility = View.GONE
            }
        }
    }
}