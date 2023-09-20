package com.oceantech.tracking.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.TrackingApplication
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.databinding.FragmentMyProfileBinding
import com.oceantech.tracking.ui.home.HomeViewModel
import javax.inject.Inject
//done
class MyProfileFragment : TrackingBaseFragment<FragmentMyProfileBinding>() {
    //viewModel
    private val viewModel: HomeViewModel by activityViewModel()
    //dependency
    @Inject
    lateinit var sessionManager: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity().application as TrackingApplication).trackingComponent.inject(this)
        super.onViewCreated(view, savedInstanceState)
        listenEvent()
    }
    private fun listenEvent() {
        views.btnNavigate.setOnClickListener {
            findNavController().navigate(R.id.action_nav_fofileFragment_to_editProfileFragment)
        }
        views.logout.setOnClickListener {
            viewModel.handleEventLogout()
        }
        sessionManager.getDarkMode().let {
            views.switchDarkMode.isChecked=it
        }
        views.switchDarkMode.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.handleChangeThemeMode(isChecked)
        }
        views.layoutLocation.setOnClickListener {
            findNavController().navigate(R.id.action_nav_fofileFragment_to_mapsFragment)
        }
        views.layoutResetPass.setOnClickListener {
            findNavController().navigate(R.id.action_nav_fofileFragment_to_resetPasswordFragment)
        }
        views.layoutNotifies.setOnClickListener {
            findNavController().navigate(R.id.action_nav_fofileFragment_to_notifiesFragment)
        }
        views.layoutSupport.setOnClickListener {
            findNavController().navigate(R.id.action_nav_fofileFragment_to_supportFragment)
        }
    }
    private fun initUi(user: User) {
        views.apply {
            displayName.text = user.displayName
        }
    }
    override fun invalidate(): Unit = withState(viewModel) {
        when(it.userCurrent){
            is Success ->{
                it.userCurrent.invoke().let { user ->
                    initUi(user)
                }
            }
        }

    }
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMyProfileBinding {
        return FragmentMyProfileBinding.inflate(inflater,container,false)
    }

}