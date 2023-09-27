package com.oceantech.tracking.ui.users

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.TrackingApplication
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.network.SessionManager
import com.oceantech.tracking.databinding.FragmentDetailUsersBinding
import com.oceantech.tracking.databinding.FragmentUsersBinding
import com.oceantech.tracking.ui.security.UserPreferences
import com.oceantech.tracking.ui.tracking.ActionType
import com.oceantech.tracking.utils.checkStatusApiRes
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class UsersFragment : TrackingBaseFragment<FragmentUsersBinding>() {
    private val usersViewModel: UsersViewModel by activityViewModel()
    private var role: String = "ROLE_USER"

    @Inject
    lateinit var userPreferences: UserPreferences

    @Inject
    lateinit var sessionManager: SessionManager

    lateinit var adapter: UserAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as TrackingApplication).trackingComponent.inject(
            this
        )
        usersViewModel.handle(UsersViewAction.RefeshUserAction(lifecycleScope))
    }

    private fun handleEvents(it: UsersViewEvent) {
        when (it) {
            is UsersViewEvent.ReturnDetailViewEvent -> {
                val action =
                    UsersFragmentDirections.actionNavUsersFragmentToNextUpdateFragment(
                        it.user,
                        isAdmin = true
                    )
                findNavController().navigate(action)
            }

            else -> {
                false
            }
        }
    }


    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentUsersBinding {
        return FragmentUsersBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        usersViewModel.observeViewEvents {
            if (it != null) {
                handleEvents(it)
            }
        }
        setUpRCV()
    }

    private fun setUpRCV() {
        adapter = UserAdapter(action)
        views.recyclerView.setHasFixedSize(true)
        views.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        views.recyclerView.adapter = adapter
    }

    private val action: (User, ActionType) -> Unit = { user, actionType ->
        // Lắng nghe sự thay đổi của userFullname
        if (actionType == ActionType.EDIT) {
            lifecycleScope.launch {
                userPreferences.userRole.collect { userRole ->
                    // Dữ liệu userFullname đã thay đổi, bạn có thể làm gì đó với nó ở đây
                    if (userRole != null && userRole != role) {
                        // Đã có dữ liệu userFullname, hiển thị nó hoặc thực hiện các tác vụ khác
                        usersViewModel.handleReturnDetailUser(user)
                    } else {
                        Toast.makeText(
                            requireActivity(),
                            "Quyền truy cập bị từ chối",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }
        } else {
            showUserDetailDialog(user)
        }

    }

    override fun invalidate() = withState(usersViewModel) {
        when (it.pageUsers) {
            is Success -> {
                val pageUser = it.pageUsers.invoke()
                adapter.submitData(lifecycle, pageUser)
                Timber.e("UsersFragment Success: $pageUser")
                Toast.makeText(requireContext(), getString(R.string.success), Toast.LENGTH_SHORT)
                    .show()
            }

            is Fail -> {
                Timber.e("UsersFragment invalidate Fail:")
                Toast.makeText(
                    requireContext(), getString(checkStatusApiRes(it.pageUsers)), Toast.LENGTH_SHORT
                ).show()
            }

            else -> {}
        }
    }

    private fun showUserDetailDialog(user: User) {
        val binding = FragmentDetailUsersBinding.inflate(layoutInflater)

        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setView(binding.root)
        val alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // Hiển thị dialog
        alertDialog.show()

        binding.displayName.text = user.displayName
        binding.email.text = user.email
        binding.gender.text = user.gender
        binding.firstName.text = user.firstName
        binding.lastname.text = user.lastName
        binding.amountOfLeaveDays.text = user.countDayCheckin.toString()
        binding.trackings.text = user.countDayTracking.toString()
        binding.university.text = user.university
        binding.username.text = user.username
        binding.year.text = user.year.toString()
        binding.exit.setOnClickListener {
            // Khi người dùng bấm vào nút "exit", đóng dialog
            alertDialog.dismiss()
        }
    }
}

enum class ActionType {
    EDIT
}