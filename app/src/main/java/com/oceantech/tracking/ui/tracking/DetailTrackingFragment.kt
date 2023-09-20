package com.oceantech.tracking.ui.tracking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.Notify
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.databinding.FragmentDetailTrackingBinding
import com.oceantech.tracking.utils.NotificationDialogFragment.Companion.FALURE_ID
import com.oceantech.tracking.utils.NotificationDialogFragment.Companion.SUCCESS_ID
import com.oceantech.tracking.utils.StringUltis.dateFormat
import com.oceantech.tracking.utils.StringUltis.dateIso8601Format
import com.oceantech.tracking.utils.StringUltis.dateIso8601Format2
import com.oceantech.tracking.utils.StringUltis.dateTimeFormat
import com.oceantech.tracking.utils.convertDateToStringFormat
import com.oceantech.tracking.utils.convertToStringFormat
import com.oceantech.tracking.utils.showConfirmationDialog
import com.oceantech.tracking.utils.showDialog
import nl.joery.animatedbottombar.AnimatedBottomBar
import java.util.Date
//done
class DetailTrackingFragment : TrackingBaseFragment<FragmentDetailTrackingBinding>() {
    val viewModel: TrackingSubViewModel by activityViewModel()
    //views
    private lateinit var bottomNavigation: AnimatedBottomBar
    //data
    private val args: DetailTrackingFragmentArgs by navArgs()
    var mTracking: Tracking? = null
    var state: Int = 0
    lateinit var notify: Notify

    companion object {
        private const val UPDATE = 3
        private const val DELETE = 4
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDetailTrackingBinding {
        return FragmentDetailTrackingBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mTracking = args.trackingArg
        setupUi()
        listenEvent()
    }

    private fun listenEvent() {
        views.btnDelete.setOnClickListener {
            requireActivity().showConfirmationDialog{
                viewModel.handle(TrackingViewAction.DeleteTracking(mTracking!!.id))
                state = DELETE
            }
        }
        views.btnSave.setOnClickListener {
            requireActivity().showConfirmationDialog {
                mTracking.apply {
                    this?.content = views.trackingContent.text.toString()
                    this?.date = Date().convertDateToStringFormat(dateIso8601Format2)
                }
                viewModel.handle(TrackingViewAction.UpdateTracking(mTracking!!.id, mTracking!!))
                state = UPDATE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setupTurnOff()
    }

    private fun setupTurnOff(){
        bottomNavigation= requireActivity().findViewById(R.id.bottomNavigationView)
        bottomNavigation.visibility= View.GONE
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24)
        }
    }

    private fun setupUi() {
        mTracking?.let {
            views.displayName.text="${mTracking?.user?.displayName}"
            views.trackingContent.setText(it.content)
            views.trackingTime.text = it.date.convertToStringFormat(dateIso8601Format,dateTimeFormat)
            views.trackingDate.text = it.date.convertToStringFormat(dateIso8601Format,dateFormat)
        }
    }

    override fun onDestroyView() {
        bottomNavigation.visibility=View.VISIBLE
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        super.onDestroyView()
    }

    override fun invalidate(): Unit = withState(viewModel) {
        when (state) {
            DELETE -> handleStateDeleteTracking(it)
            UPDATE -> handleStateUpdateTracking(it)
        }
    }

    private fun handleStateUpdateTracking(it: TrackingViewState) {
        when (it.asyncUpdateTracking) {
            is Success -> {
                notify= Notify(SUCCESS_ID,getString(R.string.update_tracking_successfully))
                showDialog(notify,requireActivity().supportFragmentManager)
                findNavController().popBackStack()
            }

            is Fail -> {
                notify= Notify(FALURE_ID,getString(R.string.update_tracking_unsuccessfully))
                showDialog(notify,requireActivity().supportFragmentManager)
            }
        }
    }

    private fun handleStateDeleteTracking(it: TrackingViewState) {
        when (it.asyncDeleteTracking) {
            is Success -> {
                notify= Notify(SUCCESS_ID,getString(R.string.delete_tracking_successfully))
                showDialog(notify,requireActivity().supportFragmentManager)
                findNavController().popBackStack()
            }
            is Fail -> {
                notify= Notify(FALURE_ID,getString(R.string.delete_tracking_unsuccessfully))
                showDialog(notify,requireActivity().supportFragmentManager)
            }
        }
    }

}