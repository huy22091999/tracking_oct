package com.oceantech.tracking.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.*
import com.bumptech.glide.Glide
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.core.TrackingClickItem
import com.oceantech.tracking.data.model.Menu2
import com.oceantech.tracking.databinding.FragmentProfileBinding
import com.oceantech.tracking.utils.getOptionInfo
import com.oceantech.tracking.utils.showSnackbar

class ProfileFragment : TrackingBaseFragment<FragmentProfileBinding>() {

    private val infoViewModel: InfoViewModel by activityViewModel()
    lateinit var adapter: InfoAdapter

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileBinding {
        return FragmentProfileBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRcv()
        listenClickUI()
    }

    override fun invalidate() = withState(infoViewModel){
        when(it.myUserUser){
            is Success ->{
                infoViewModel.handle(InfoViewsAction.CheckIsAdmin(it.myUserUser.invoke()))
            }
            is Fail ->{
            showSnackbar(views.root, getString(R.string.failed), null, R.color.red){}
            }
            else ->{

            }
        }
        when(it.userEdit){
            is Success ->{
                var user = it.userEdit.invoke()
                views.tvName.text = user.displayName
                views.tvIndexCheckin.text = "${user.countDayCheckin ?: getString(R.string.nodata)}"
                views.tvIndexTracking.text = "${user.countDayTracking ?: getString(R.string.nodata)}"
                Glide.with(this).load("user.link").placeholder(R.drawable.ic_person).into(views.imgAvt)
                adapter.setDataUser(user)

                views.swipeLayout.isRefreshing = false
            }
            is Fail ->{
                showSnackbar(views.root, getString(R.string.failed), null, R.color.red){}
            }
            else ->{

            }
        }

        when(it.updateUser){
            is Success ->{
                showSnackbar(views.root, getString(R.string.success), null, R.color.text_title1){}
                infoViewModel.handle(InfoViewsAction.RemoveUpdateUserAction)
            }

            is Fail ->{
                showSnackbar(views.root, getString(R.string.failed), null, R.color.red){}
                infoViewModel.handle(InfoViewsAction.RemoveUpdateUserAction)
            }

            else ->{

            }
        }

        (it.isMyProfile || it.isAdmin).let {isCheck ->
                views.btnEdit.isVisible = isCheck
                views.btnOption.isVisible = isCheck
        }
    }

    private fun listenClickUI() {
        views.btnEdit.setOnClickListener{
            infoViewModel.RetunNavigateToFrg(R.id.editProfileFragment)
        }

        views.btnOption.setOnClickListener{
            withState(infoViewModel){
                showMenuBottomDialog()
            }
        }

        views.swipeLayout.setOnRefreshListener {
            withState(infoViewModel){
                infoViewModel.handle(InfoViewsAction.GetUserCurentByID(it.userEdit.invoke()?.id, it.isMyProfile))
            }

        }
    }

    private fun setupRcv() {
        adapter = InfoAdapter(object : TrackingClickItem(){

        })
        views.rcvDetail.adapter = adapter
        views.rcvDetail.layoutManager = LinearLayoutManager(requireContext())
    }


    private fun showMenuBottomDialog(){
        var isActive = withState(infoViewModel){ it.userEdit.invoke()?.active ?: false}
        MenuBottomSheetDialogFragment.getInstanceMenu2(
            true,
            getOptionInfo(requireContext(), isActive),
            object : TrackingClickItem(){
                override fun onItemMenu2ClickListenner(menu2: Menu2) {
                    super.onItemMenu2ClickListenner(menu2)
                    when(menu2.id){
                        0 ->{
                            infoViewModel.RetunNavigateToFrg(R.id.changePassFragment)
                        }
                        1 ->{
                            infoViewModel.handle(InfoViewsAction.BlockUser)
                        }
                        2-> {
                            infoViewModel.handle(InfoViewsAction.UnblockUser)
                        }
                    }
                }
            }
        ).show(requireActivity().supportFragmentManager, MenuBottomSheetDialogFragment.TAG)
    }


}