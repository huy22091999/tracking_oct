package com.oceantech.tracking.ui.profile

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.airbnb.mvrx.viewModel
import com.oceantech.tracking.R
import com.oceantech.tracking.TrackingApplication
import com.oceantech.tracking.core.TrackingBaseActivity
import com.oceantech.tracking.databinding.ActivityInfomationBinding
import com.oceantech.tracking.utils.popActivityAnim
import javax.inject.Inject

@SuppressLint("RestrictedApi")
class InfoActivity : TrackingBaseActivity<ActivityInfomationBinding>(), InfoViewModel.Factory {

    override fun getBinding(): ActivityInfomationBinding {
        return ActivityInfomationBinding.inflate(layoutInflater)
    }

    lateinit var bindingActivity: ActivityInfomationBinding
    private lateinit var navController: NavController
    private val infoViewModel: InfoViewModel by viewModel()

    @Inject
    lateinit var infoViewModelFactory: InfoViewModel.Factory


    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TrackingApplication).trackingComponent.inject(this)
        super.onCreate(savedInstanceState)
        bindingActivity = views
        setupToolbar()
        setupNav()

        val userId: String? = intent.extras?.getString("userID")

        infoViewModel.handle(InfoViewsAction.GetMyUserAction)
        if (userId != null) {
            infoViewModel.handle(InfoViewsAction.GetUserCurentByID(userId, false))
        } else {
            infoViewModel.handle(InfoViewsAction.GetUserCurentAction)
        }

        infoViewModel.subscribe(this) {

        }

        infoViewModel.observeViewEvents {
            when (it) {
                is InfoViewsEvent.ReturnNavigateToFrgViewEvent -> navigateTo(it.id)
                is InfoViewsEvent.ReturnBacktoFrgViewEvent -> navController.popBackStack()
            }

        }
    }

    private fun setupToolbar() {
        views.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        setSupportActionBar(views.toolbar)
        supportActionBar?.title = getString(R.string.infomation);
    }

    private fun setupNav() {
        navController = findNavController(R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener{ _, destination, _ ->
            when(destination.id){
                R.id.profileFragment ->{
                    supportActionBar?.title = getString(R.string.infomation)

                }
                R.id.editProfileFragment ->{
                    supportActionBar?.title = getString(R.string.edit)
                }
                R.id.changePassFragment ->{
                    supportActionBar?.title = getString(R.string.change_pass)
                }
            }
        }
    }

    private fun navigateTo(id: Int){
        navController.navigate(id)
    }

    override fun onPause() {
        popActivityAnim()
        super.onPause()
    }

    override fun create(initialState: InfoViewsState): InfoViewModel {
        return infoViewModelFactory.create(initialState)
    }


}