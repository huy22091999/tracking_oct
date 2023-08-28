package com.oceantech.tracking.ui.tracking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.databinding.FragmentDetailTrackingBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone


class DetailTrackingFragment : TrackingBaseFragment<FragmentDetailTrackingBinding>() {
    val viewModel: TrackingSubViewModel by activityViewModel()
    val args: DetailTrackingFragmentArgs by navArgs()
    var mTracking: Tracking? = null
    var state: Int = 0

    companion object {
        private const val UPDATE = 3;
        private const val DELETE = 4;
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDetailTrackingBinding {
        return FragmentDetailTrackingBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireContext() as AppCompatActivity).supportActionBar?.hide()
        mTracking = args.trackingArg
        setupUi()
        views.btnDelete.setOnClickListener {
            viewModel.handle(TrackingViewAction.DeleteTracking(mTracking!!.id))
            state = DELETE
        }
        views.btnSave.setOnClickListener {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            mTracking.apply {
                this?.content = views.trackingContent.text.toString()
                this?.date = dateFormat.format(Calendar.getInstance().time).toString()
            }
            viewModel.handle(TrackingViewAction.UpdateTracking(mTracking!!.id, mTracking!!))
            state = UPDATE
        }
    }

    private fun setupUi() {
        mTracking?.let {
            views.trackingContent.setText(it.content)
            views.time.text = it.date
        }
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
                Toast.makeText(
                    requireContext(),
                    getString(R.string.update_tracking_successfully),
                    Toast.LENGTH_LONG
                ).show()
                findNavController().popBackStack()
            }

            is Fail -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.update_tracking_unsuccessfully),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun handleStateDeleteTracking(it: TrackingViewState) {
        when (it.asyncDeleteTracking) {
            is Success -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.delete_tracking_successfully),
                        Toast.LENGTH_LONG
                    ).show()
                findNavController().popBackStack()
            }
            is Fail -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.delete_tracking_unsuccessfully),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireContext() as AppCompatActivity).supportActionBar?.show()
    }

}