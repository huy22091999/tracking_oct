package com.oceantech.tracking.ui.users

import android.app.AlertDialog
import android.graphics.drawable.Drawable
import android.os.Bundle
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
import com.oceantech.tracking.databinding.FragmentNextUpdateBinding
import com.oceantech.tracking.utils.checkStatusApiRes
import com.oceantech.tracking.utils.validateEmail

class NextUpdateFragment : TrackingBaseFragment<FragmentNextUpdateBinding>() {
    private val usersViewModel: UsersViewModel by activityViewModel()
    lateinit var user: User
    var isAdmin: Boolean = false
    var isBlock: Boolean = false
    private lateinit var dialog: AlertDialog
    var active: Boolean = false
    lateinit var newDisplayName: String
    lateinit var newEmail: String
    lateinit var newUserName: String
    lateinit var newPassword: String
    lateinit var newConfirmPassword: String

    private val args: NextUpdateFragmentArgs by navArgs()
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNextUpdateBinding = FragmentNextUpdateBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = args.ReviveUser1
        isAdmin = args.isAdmin
//
        setData()
    }

    private fun handleEvent(it: UsersViewEvent) {
        when (it) {
            //is HomeViewEvent.ResetTheme -> {}
            //is HomeViewEvent.ResetLanguege -> {}
            else -> {
                false
            }
        }
    }


    private fun setData() {
        views.apply {
            displayName.setText(user.displayName.toString())
            email.setText(user.email.toString())
            username.setText(user.username.toString())
            if (user.active == true) {
                isBlock = false
                val drawableId = R.drawable.img_unlock
                lock.setImageResource(drawableId)
                btnLock.text = resources.getString(R.string.lock)
            } else {
                isBlock = true
                val drawableId = R.drawable.img_lock
                lock.setImageResource(drawableId)
                btnLock.text = resources.getString(R.string.unlock)
            }
            send.setOnClickListener {
                updateProfile()
            }
            btnLock.setOnClickListener {
                lockUser()
            }
        }
    }

    private fun lockUser() {
        user.apply {
            this.active = true
        }
        if (isBlock == true) {
            usersViewModel.handle(UsersViewAction.EditUser(user.id!!.toInt(), user))
        } else {
            val id = user.id
            id?.toInt()?.let { UsersViewAction.blockUser(it) }?.let { usersViewModel.handle(it) }
        }
//        val id = user.id
//        id?.toInt()?.let { UsersViewAction.blockUser(it) }?.let { usersViewModel.handle(it) }
    }


    private fun updateProfile() {
        newDisplayName = views.displayName.text.toString().trim()
        newEmail = views.email.text.toString().trim()
        newUserName = views.username.text.toString().trim()
        newPassword = views.password.text.toString().trim()
        newConfirmPassword = views.confrimPassword.text.toString().trim()

        if (newDisplayName.isNullOrEmpty()) views.displayName.error =
            requireContext().getString(R.string.username_not_empty)
        if (!validateEmail(newEmail)) views.email.error =
            requireContext().getString(R.string.email_not_correct)
        if (newUserName.isNullOrEmpty()) views.username.error =
            requireContext().getString(R.string.username_not_empty)
        if (newPassword.isNullOrEmpty()) views.password.error =
            requireContext().getString(R.string.username_not_empty)
        if (newConfirmPassword.isNullOrEmpty()) views.confrimPassword.error =
            requireContext().getString(
                R.string.username_not_empty
            )
        if (newPassword != newConfirmPassword) views.confrimPassword.error =
            requireContext().getString(R.string.confirm_password_not_correct)

        if (!newDisplayName.isNullOrEmpty() && validateEmail(newEmail) && !newUserName.isNullOrEmpty() && !newPassword.isNullOrEmpty() && !newConfirmPassword.isNullOrEmpty()) {
            if (newPassword == newConfirmPassword) {
                user.apply {
                    this.displayName = newDisplayName
                    this.email = newEmail
                    this.username = newUserName
                    this.password = newPassword
                    this.confirmPassword = newConfirmPassword
                    this.changePass = true
                }
                if (isAdmin) {
                    usersViewModel.handle(UsersViewAction.EditUser(user.id!!.toInt(), user))
                }
            }
        }
    }

    override fun invalidate(): Unit = withState(usersViewModel) {
        when (it.userEdit) {
            is Success -> {
                val active = it.userEdit.invoke()?.active
                if (active == true) {
                    isBlock = false
                    val drawableId = R.drawable.img_unlock
                    views.lock.setImageResource(drawableId)
                    views.btnLock.text = resources.getString(R.string.lock)
                }
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.success),
                    Toast.LENGTH_SHORT
                ).show()
                usersViewModel.handleRemoveStateBlockUser()
            }

            is Fail -> {
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.fail),
                    Toast.LENGTH_SHORT
                ).show()
                usersViewModel.handleRemoveStateBlockUser()
//                Toast.makeText(
//                    requireContext(), getString(checkStatusApiRes(it.userEdit)), Toast.LENGTH_SHORT
//                ).show()
            }

            else -> {
                false
            }
        }
        when (it.blockUser) {
            is Success -> {
                val active = it.blockUser.invoke()?.active
                if (active == false) {
                    isBlock = true
                    val drawableId = R.drawable.img_lock
                    views.lock.setImageResource(drawableId)
                    views.btnLock.text = resources.getString(R.string.unlock)
                }
                usersViewModel.handleRemoveStateBlockUser()
            }

            is Fail -> {
                usersViewModel.handleRemoveStateBlockUser()
                Toast.makeText(
                    requireContext(), getString(checkStatusApiRes(it.blockUser)), Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}