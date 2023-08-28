package com.oceantech.tracking.ui.infomation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.isVisible
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.bumptech.glide.Glide
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.FragmentInfomationBinding
import com.oceantech.tracking.utils.*
import timber.log.Timber

class InfomationFragment : TrackingBaseFragment<FragmentInfomationBinding>(){

    var user : User? = null
    var isEdit = false

    private val infoViewModel: InfoViewModel by activityViewModel()

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentInfomationBinding {
        return FragmentInfomationBinding.inflate(inflater, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        infoViewModel.handle(InfoViewsAction.GetUserAction)
        listentUIClick()

        infoViewModel.observeViewEvents {
            handleViewEvent(it)

        }
    }

    private fun handleViewEvent(it: InfoViewsEvent) {
        when(it){
            is InfoViewsEvent.ReturnEditEvent ->{
                isEdit = !isEdit
                handleEditUser()
            }
        }
    }

    private fun listentUIClick(){
        views.imgOption.setOnClickListener{
            if (user != null) showPopMenu(it)
        }

        views.btnAccept.setOnClickListener{
            val resources = resources
            if (checkTILNull(resources, views.edtEmail) or checkTILNull(resources, views.edtEducation)
                or checkValidEmail(resources, views.edtEmail)){
            }else{
                if (user != null && isEdit){
                    user!!.displayName = views.edtName.text.toString().trim()
                    user!!.email = views.edtEmail.text.toString().trim()
                    user!!.university = views.edtEducation.text.toString().trim()
                    infoViewModel.handle(InfoViewsAction.UpdateUserAction(user!!))
                }
            }

        }
    }

    private fun showPopMenu(view : View){
        val popUpMenu = PopupMenu(requireContext(), view)
        popUpMenu.inflate(R.menu.menu_option)
        popUpMenu.show()

        popUpMenu.setOnMenuItemClickListener{
            when(it.itemId){
                R.id.edit_info ->{
                    infoViewModel.handleReturnEditUser()
                }
                R.id.add_image ->{}
            }
            true
        }
    }

    private fun handleEditUser() {
        if (isEdit){
            replaceViewVisible(views.tvName, views.edtName)
            replaceViewVisible(views.tvEmail, views.edtEmail)
            replaceViewVisible(views.tvEducation, views.edtEducation)
            setVisibleView(views.btnAccept, true)
            views.edtName.setText(user?.displayName)
            views.edtEmail.setText(user?.email)
            views.edtEducation.setText(user?.university)
        }else{
            setVisibleView(views.btnAccept, false)
            replaceViewVisible(views.edtName, views.tvName)
            replaceViewVisible( views.edtEmail, views.tvEmail)
            replaceViewVisible(views.edtEducation, views.tvEducation)
        }

    }

    override fun invalidate() = withState(infoViewModel){
            when(it.user){
                is Success ->{
                    user = it.user.invoke()
                    Timber.e("UsersFragment Success: $user")
                    Glide.with(requireContext()).load("user.link").placeholder(R.drawable.ic_person).into(views.imgAvt)
                    views.tvName.text = user?.displayName
                    views.tvEmail.text = user?.email
                    views.tvEducation.text = user?.university

                    if (isEdit) {
                        isEdit = false
                        handleEditUser()
                    }
                    Toast.makeText(requireContext(), getString(R.string.success), Toast.LENGTH_SHORT).show()
                }
                is Fail -> {
                    Toast.makeText(requireContext(), getString(checkStatusApiRes(it.user)), Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }


}