package com.oceantech.tracking.ui.profile

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.airbnb.mvrx.Fail
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
import com.oceantech.tracking.ui.home.HomeViewEvent
import com.oceantech.tracking.ui.home.HomeViewModel
import com.oceantech.tracking.ui.security.SecurityViewAction
import com.oceantech.tracking.utils.initialAlertDialog
import com.oceantech.tracking.utils.validateEmail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NextUpdateFragment : TrackingBaseFragment<FragmentNextUpdateBinding>(){
    private val viewModel:HomeViewModel by activityViewModel()
    lateinit var user: User
    var isMyself:Boolean = false
    private lateinit var dialog: AlertDialog

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

        dialog = initialAlertDialog(requireContext(),
            confirmUpdate, refuseUpdate,
            requireContext().getString(R.string.confirm_update),
            requireContext().getString(R.string.ok),
            requireContext().getString(R.string.back)
        )

        viewModel.observeViewEvents {
            handleEvent(it)
        }
        user = args.user!!
        isMyself = args.isMyself
        views.backLayout.setOnClickListener {
            if(isMyself){
                viewModel.handleReturnUpdateInfo(user)
            } else {
                viewModel.handleReturnEditInfo(user)
            }
        }
        setData()
    }

    private fun handleEvent(it: HomeViewEvent) {
        when(it){
            is HomeViewEvent.ResetTheme -> {}
            is HomeViewEvent.ResetLanguege -> {}
        }
    }


    private fun setData() {
        views.apply {
            displayName.setText(user.displayName.toString())
            email.setText(user.email.toString())
            username.setText(user.username.toString())

            send.setOnClickListener {
                dialog.show()
            }
        }
    }

    private val confirmUpdate:()->Unit={
        updateProfile()
    }

    private val refuseUpdate:()->Unit={
        dialog.dismiss()
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

        if (!newDisplayName.isNullOrEmpty() && validateEmail(newEmail) && !newUserName.isNullOrEmpty() && !newPassword.isNullOrEmpty() && !newConfirmPassword.isNullOrEmpty()) {
            if (newPassword == newConfirmPassword){
                user.apply {
                    this.displayName = newDisplayName
                    this.email = newEmail
                    this.username =newUserName
                    this.password = newPassword
                    this.confirmPassword = newConfirmPassword
                    this.changePass = true
                }
                if(isMyself){
                    viewModel.handle(HomeViewAction.UpdateMyself(user))
                } else {
                    viewModel.handle(HomeViewAction.EditUser(user.id!!.toInt(), user))
                }
            }
        }
    }

    override fun invalidate():Unit = withState(viewModel){
        when(it.asyncUpdateMySelf){
            is Success -> {
                it.asyncUpdateMySelf?.invoke().let {
                    Toast.makeText(requireContext(), requireContext().getString(R.string.tracking_success), Toast.LENGTH_SHORT).show()
                    viewModel.handleReturnProfile()
                    viewModel.handleRemoveStateUpdateMySelf()
                }
            }
            is Fail -> {
                Toast.makeText(requireContext(),requireContext().getString(R.string.block_account_fail), Toast.LENGTH_SHORT).show()
            }
        }
        when(it.asyncEditUser){
            is Success -> {
                Toast.makeText(requireContext(), requireContext().getString(R.string.tracking_success), Toast.LENGTH_SHORT).show()
                viewModel.handleReturnUsers()
                viewModel.handleRemoveStateEditUser()
            }
            is Fail -> {
                Toast.makeText(requireContext(),requireContext().getString(R.string.block_account_fail), Toast.LENGTH_SHORT).show()
            }
        }
    }
}