package com.oceantech.tracking.ui.user

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.FragmentDetailUserBinding
import com.oceantech.tracking.ui.home.HomeViewAction
import com.oceantech.tracking.ui.home.HomeViewModel

class DetailUserFragment : TrackingBaseFragment<FragmentDetailUserBinding>() {
    private val viewModel:HomeViewModel by activityViewModel()
    private val args: DetailUserFragmentArgs by navArgs()
    private lateinit var user: User

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDetailUserBinding = FragmentDetailUserBinding.inflate(inflater,container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = args.infoUser!!
        setData(user)

        views.container.visibility = View.VISIBLE
        views.progressBar.visibility = View.GONE

        views.lockButton.setOnClickListener {
            viewModel.handle(HomeViewAction.BlockUser(user.id!!.toInt()))
            Log.e("id of account now:", "${user.id!!.toInt()}")
        }
        views.updateButton.setOnClickListener {
            viewModel.handleReturnUpdateInfo(user)
        }
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
        }
    }

    override fun invalidate():Unit = withState(viewModel){
        when(it.asyncBlockUser){
            is Success -> {
                viewModel.handleRemoveStateBlockUser()
                viewModel.handleReturnUsers()
                Toast.makeText(requireContext(), requireContext().getString(R.string.block_account_success), Toast.LENGTH_SHORT).show()
            }
            is Fail -> {
                views.container.visibility = View.VISIBLE
                views.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), requireContext().getString(R.string.block_account_fail), Toast.LENGTH_SHORT).show()
            }
            is Loading -> {
                viewModel.handleRemoveStateBlockUser()
                viewModel.handle(HomeViewAction.GetAllUsers)
                views.container.visibility = View.GONE
                views.progressBar.visibility = View.VISIBLE
            }
        }
    }

}