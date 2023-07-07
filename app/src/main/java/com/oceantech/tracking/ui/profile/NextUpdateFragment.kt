package com.oceantech.tracking.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.network.getDeviceToken
import com.oceantech.tracking.databinding.FragmentNextSigninBinding
import com.oceantech.tracking.databinding.FragmentNextUpdateBinding
import com.oceantech.tracking.ui.home.HomeViewAction
import com.oceantech.tracking.ui.home.HomeViewModel
import com.oceantech.tracking.ui.security.SecurityViewAction
import com.oceantech.tracking.utils.validateEmail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NextUpdateFragment : TrackingBaseFragment<FragmentNextUpdateBinding>(){
    private val viewModel:HomeViewModel by activityViewModel()
    lateinit var user: User

    lateinit var newDisplayName:String
    lateinit var newEmail:String
    lateinit var newUserName:String
    lateinit var newPassword:String
    lateinit var newConfirmPassword:String

    private val args:NextUpdateFragmentArgs by navArgs()
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNextUpdateBinding = FragmentNextUpdateBinding.inflate(inflater,container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = args.user

        setData()
    }

    private fun setData() {
        views.apply {
            displayName.setText(user.displayName.toString())
            email.setText(user.email.toString())
            username.setText(user.username.toString())

            send.setOnClickListener {
                updateProfile()
            }
        }
    }

    private fun updateProfile() {
        newDisplayName = views.displayName.text.toString().trim()
        newEmail = views.email.text.toString().trim()
        newUserName = views.username.text.toString().trim()
        newPassword = views.password.text.toString().trim()
        newConfirmPassword = views.confrimPassword.text.toString().trim()

        if(newDisplayName.isNullOrEmpty()) views.displayName.error = requireContext().getString(R.string.username_not_empty)
        if(!validateEmail(newEmail)) views.email.error = requireContext().getString(R.string.email_not_correct)
        if(newUserName.isNullOrEmpty()) views.username.error = requireContext().getString(R.string.username_not_empty)
        if(newPassword.isNullOrEmpty()) views.password.error = requireContext().getString(R.string.username_not_empty)
        if(newConfirmPassword.isNullOrEmpty()) views.confrimPassword.error = requireContext().getString(
            R.string.username_not_empty)
        if (newPassword != newConfirmPassword) views.confrimPassword.error = requireContext().getString(R.string.confirm_password_not_correct)
        if (!newDisplayName.isNullOrEmpty() || validateEmail(newEmail) || newUserName.isNullOrEmpty() || newPassword.isNullOrEmpty() || newConfirmPassword.isNullOrEmpty()) {
            user.apply {
                this.displayName = newDisplayName
                this.email = newEmail
                this.username =newUserName
                this.password = newPassword
                this.confirmPassword = newConfirmPassword
            }

            viewModel.handle(HomeViewAction.UpdateMyself(user))
        }
    }

    override fun invalidate():Unit = withState(viewModel){
        when(it.asyncUpdateMySelf){
            is Success -> {
                it.asyncUpdateMySelf?.invoke().let {
                    Toast.makeText(requireContext(), requireContext().getString(R.string.tracking_success), Toast.LENGTH_SHORT).show()
                    viewModel.handleReturnProfile()
                }
            }
        }
    }
}