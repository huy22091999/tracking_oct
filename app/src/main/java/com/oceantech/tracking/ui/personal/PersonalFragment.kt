package com.oceantech.tracking.ui.personal

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.FragmentPersonalBinding
import com.oceantech.tracking.ui.home.HomeViewAction
import com.oceantech.tracking.ui.home.HomeViewModel

class PersonalFragment : TrackingBaseFragment<FragmentPersonalBinding>() {

    private val viewModel: HomeViewModel by activityViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.handle(HomeViewAction.GetCurrentUser)
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPersonalBinding {
        return FragmentPersonalBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun invalidate() = withState(viewModel){
        when(it.userCurrent){
            is Success -> {
                views.personPB.visibility = View.GONE
                views.personInfor.visibility = View.VISIBLE
                views.txtPersonDisplayName.visibility = View.VISIBLE
                views.imgPerson.visibility = View.VISIBLE
                bindUser(it.userCurrent.invoke())

            }
            is Loading -> {
                views.personPB.visibility = View.VISIBLE
                views.personInfor.visibility = View.GONE
                views.txtPersonDisplayName.visibility = View.GONE
                views.imgPerson.visibility = View.GONE
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindUser(user: User) {
        views.txtPersonId.text = "Id: ${user.id}"
        views.txtFullName.text = "${user.firstName} ${user.lastName}"
        views.txtPersonUsermame.text = "Username: ${user.username}"
        views.txtPersonDisplayName.text = "Display Name: ${user.displayName}"
    }
}