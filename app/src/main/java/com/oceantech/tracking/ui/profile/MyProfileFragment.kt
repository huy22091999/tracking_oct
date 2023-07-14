package com.oceantech.tracking.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.FragmentMyProfileBinding
import com.oceantech.tracking.ui.home.HomeViewAction
import com.oceantech.tracking.ui.home.HomeViewEvent
import com.oceantech.tracking.ui.home.HomeViewModel

class MyProfileFragment : TrackingBaseFragment<FragmentMyProfileBinding>() {
    private val viewModel:HomeViewModel by activityViewModel()
    private var user:User? = null
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMyProfileBinding = FragmentMyProfileBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.handle(HomeViewAction.GetCurrentUser)

        viewModel.observeViewEvents {
            handleEvents(it)
        }
    }

    private fun handleEvents(it: HomeViewEvent) {
        when(it){
            is HomeViewEvent.ResetLanguege -> {
                views.apply {
                    displayNameLabel.text = requireContext().getText(R.string.display_name)
                    userNameLabel.text = requireContext().getText(R.string.username)
                    genderLabel.text = requireContext().getText(R.string.gender)
                    universityLabel.text = requireContext().getText(R.string.university)
                    yearLabel.text = requireContext().getText(R.string.year)
                    leaveDaysLabel.text = requireContext().getText(R.string.amount_of_leave_days)
                    trackingDaysLabel.text = requireContext().getText(R.string.trackings_of_participations)
                }
            }
            is HomeViewEvent.ResetTheme -> {

            }
        }
    }

    private fun setData(user: User){
        views.apply {
            email.text = user.email
            displayName.text = user.displayName
            username.text = user.username
            fullName.text = "${user.firstName} ${user.lastName}"
            gender.text = user.gender
            university.text = user.university
            year.text = user.year.toString()
        }
    }

    override fun invalidate():Unit = withState(viewModel){
        when(it.userCurrent){
            is Success -> {
                it.userCurrent.invoke()?.let { user1 ->
                    user = user1
                    views.apply {
                        setData(user!!)
                    }
                    views.updateButton.setOnClickListener {
                        viewModel.handleReturnUpdateInfo(user!!)
                        Log.i("check data: ", user!!.toString())
                    }
                }
            }
        }
    }
}
