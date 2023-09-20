package com.oceantech.tracking.ui.profile.changepass

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.FragmentChangePassBinding
import com.oceantech.tracking.ui.profile.InfoViewModel
import com.oceantech.tracking.ui.profile.InfoViewsAction
import com.oceantech.tracking.utils.checkTILNull
import com.oceantech.tracking.utils.checkValidEPassword
import com.oceantech.tracking.utils.showSnackbar

class ChangePassFragment : TrackingBaseFragment<FragmentChangePassBinding>() {

    private val infoViewModel: InfoViewModel by activityViewModel()

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentChangePassBinding {
        return FragmentChangePassBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listenCLickUI()
    }

    private fun listenCLickUI() {
        views.btnCancel.setOnClickListener{
            infoViewModel.RetunBackToFrg()
        }
        
        views.btnUpdate.setOnClickListener{
            if (checkTILNull(resources, views.password) or checkTILNull(resources, views.newPassword)
                or checkTILNull(resources, views.newPassword2) or checkValidEPassword(resources, views.newPassword, views.newPassword2))
            else {
                withState(infoViewModel){
//                    verifyUser(it.user.invoke()?.username, views.password.toString())
                    updatePassword(it.userEdit.invoke())
                }

            }
        }
    }

    fun verifyUser(userName:String?, passWord: String) {
        infoViewModel.handle(InfoViewsAction.VerifyUserAction(userName, passWord))
    }

    fun updatePassword(user: User?) {
        if (user == null) return
        user.changePass = true
        user.setPassword = true
        user.password = views.newPassword2.text.toString()

        infoViewModel.handle(InfoViewsAction.UpdateUserAction(user))
    }

    override fun invalidate() = withState(infoViewModel){
        when(it.updateUser){
            is Success ->{
                showSnackbar(views.root, getString(R.string.success), null, R.color.text_title1){}
                infoViewModel.RetunBackToFrg()
            }

            is Fail ->{
                showSnackbar(views.root, getString(R.string.failed), null, R.color.red){}
            }

            else ->{

            }
        }

        when(it.userVerify){
            is Success ->{
                updatePassword(it.userEdit.invoke())
            }

            is Fail ->{
                showSnackbar(views.root, getString(R.string.failed), null, R.color.red){}
                views.password.setError(getString(R.string.login_fail))
            }

            else ->{

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        infoViewModel.handle(InfoViewsAction.RemoveUpdateUserAction)
    }
}