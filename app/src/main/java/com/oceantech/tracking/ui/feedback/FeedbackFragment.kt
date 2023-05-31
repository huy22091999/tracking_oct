package com.oceantech.tracking.ui.feedback

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.activityViewModel
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.Feedback
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.FragmentFeedbackBinding
import com.oceantech.tracking.ui.home.HomeViewAction
import com.oceantech.tracking.ui.home.HomeViewEvent
import com.oceantech.tracking.ui.home.HomeViewmodel


class FeedbackFragment : TrackingBaseFragment<FragmentFeedbackBinding>() {
    private lateinit var user: User
    val viewModel: HomeViewmodel by activityViewModel()
    private lateinit var feedback: Feedback
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFeedbackBinding {
        return FragmentFeedbackBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        withState(viewModel)
//        {
//            it.userCurrent.invoke().let {
//                user=it!!
//            }
//        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeViewEvents {
            handleEvent(it)
        }
        views.btnSendFeedback.setOnClickListener {
            feedback = Feedback(null, null, views.feedback.text.toString().trim())
            if (feedback.feedback.isNullOrBlank()) {
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.channel_description)
                    .setMessage(R.string.feedback_empty)
                    .setNegativeButton(R.string.ok, null)
                    .show()
            } else viewModel.handle(HomeViewAction.SaveFeedback(feedback))
        }
    }

    private fun handleEvent(it: HomeViewEvent) {
        when (it) {
            is HomeViewEvent.ResetLanguege -> {
                views.btnSendFeedback.text = getString(R.string.send_feedback)
                views.title1.text = getString(R.string.feedback_to_app)
                views.title2.text = getString(R.string.feedback_description)
                views.feedback.hint = getString(R.string.feedback_hint)
            }
            is HomeViewEvent.SaveFeedback -> {
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.channel_description)
                    .setMessage(R.string.received)
                    .setNegativeButton(R.string.close, null)
                    .show()
                views.feedback.text = null
            }
        }
    }
//    override fun invalidate(): Unit = withState(viewModel){
//
//
//    }
}