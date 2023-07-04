package com.oceantech.tracking.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.FragmentDetailUserBinding
import com.oceantech.tracking.ui.home.HomeViewAction
import com.oceantech.tracking.ui.home.HomeViewModel

class DetailUserFragment : TrackingBaseFragment<FragmentDetailUserBinding>() {
    private val viewModel:HomeViewModel by activityViewModel()
    private val args:DetailUserFragmentArgs by navArgs()
    private lateinit var user: User

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDetailUserBinding = FragmentDetailUserBinding.inflate(inflater,container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.infoUser == null){
            viewModel.handle(HomeViewAction.GetCurrentUser)
        }
    }

    override fun invalidate():Unit = withState(viewModel){
        when(it.userCurrent){
            is Success -> {
                it.userCurrent.invoke()?.let { user ->
                    views.apply {
                        email.text = user.email
                        displayName.text = user.displayName
                        username.text = user.username
                        lastname.text = user.lastName
                        firstName.text = user.firstName
                        gender.text = user.gender
                        university.text = user.university
                        year.text = user.year.toString()
                    }
                    if (user.roles?.last()?.authority.toString().equals("ROLE_USER")) {
                        views.lockButton.visibility = View.GONE
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