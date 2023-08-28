package com.oceantech.tracking.ui.tracking

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.*
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.databinding.FragmentBottomsheetTrackingBinding
import com.oceantech.tracking.databinding.FragmentTrackingBinding
import com.oceantech.tracking.ui.users.UsersViewAction
import com.oceantech.tracking.utils.StringUltis
import com.oceantech.tracking.utils.checkStatusApiRes
import com.oceantech.tracking.utils.convertToStringFormat
import com.oceantech.tracking.utils.convertLongToStringFormat
import timber.log.Timber
import java.text.SimpleDateFormat

class TrackingFragment : TrackingBaseFragment<FragmentTrackingBinding>() {

    companion object {
        private const val DEFAULT_STATE = 0
        private const val GET_TRACKING = 1
        private const val ADD_TRACKING = 2
        private const val UPDATE_TRACKING = 3
        private const val DELETE_TRACKING = 4
        private const val CURENT_TIME = 5
    }
    private var state = DEFAULT_STATE

    private val mViewModel: TrackingViewModel by activityViewModel()
    var adapter: TrackingAdapter? = null

    var dialog: Dialog? = null
    var dialogBinding: FragmentBottomsheetTrackingBinding? = null


    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTrackingBinding {
        return FragmentTrackingBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewModel.handle(TrackingViewAction.getAllTrackings)
        state = GET_TRACKING

        setUpRcv()
        onListenner()
    }

    private fun onListenner() {
        views.btnTracking.setOnClickListener {
            mViewModel.handleReturnShowDialogAdd(null)
        }

        mViewModel.observeViewEvents {
            handlEvent(it)
        }

        views.swipeLayout.setOnRefreshListener {
            mViewModel.handle(TrackingViewAction.getAllTrackings)
            state = GET_TRACKING
        }
    }

    private fun handlEvent(it: TrackingViewEvent) {
        when (it) {
            is TrackingViewEvent.ReturnShowDialogViewEvent -> showDialog(it.tracking)
            is TrackingViewEvent.ReturnShowOptionMenuViewEvent -> showOptionMenu(
                it.view,
                it.tracking
            )
        }
    }

    private fun setUpRcv() {
        adapter = TrackingAdapter { view, tracking ->
            mViewModel.handleReturnShowOptionMenu(view, tracking)
        }
        views.rcv.adapter = adapter
        views.rcv.layoutManager = LinearLayoutManager(requireContext())
        views.rcv.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
    }

    override fun invalidate(): Unit = withState(mViewModel) {
        when (state) {
            GET_TRACKING -> handleGetAddTrackings(it)
            ADD_TRACKING -> handleAddTracking(it)
            UPDATE_TRACKING -> handleUpdateTracking(it)
            DELETE_TRACKING -> handleDeleteTracking(it)
            CURENT_TIME -> handleCurrentTime(it)
            else -> {}
        }
    }

    private fun handleCurrentTime(it: TrackingViewState) {
        when (it.currentTime) {
            is Success -> {
                dialogBinding?.tvTime?.text =
                    it.currentTime.invoke().convertLongToStringFormat(StringUltis.dateTimeFormat)
            }
        }
    }

    private fun handleGetAddTrackings(it: TrackingViewState) {
        when (it.getTrackings) {
            is Success -> {
                adapter!!.setAllData(it.getTrackings.invoke())
                views.swipeLayout.isRefreshing = false
            }
            is Fail -> {
                Toast.makeText(requireContext(), getString(checkStatusApiRes(it.getTrackings)), Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    private fun handleAddTracking(it: TrackingViewState) {
        when (it.addTracking) {
            is Success -> {
                adapter?.addItemData(it.addTracking.invoke())
                dialog?.dismiss()
            }
            is Fail -> {
                Toast.makeText(requireContext(), getString(checkStatusApiRes(it.addTracking)), Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    private fun handleUpdateTracking(it: TrackingViewState) {
        when (it.updateTracking) {
            is Success -> {
                adapter?.updateItemData(it.updateTracking.invoke())
                dialog?.dismiss()
            }
            is Fail -> {
                Toast.makeText(requireContext(), getString(checkStatusApiRes(it.updateTracking)), Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    private fun handleDeleteTracking(it: TrackingViewState) {
        when (it.deleteTracking) {
            is Success -> {
                adapter?.deleteItemData(it.deleteTracking.invoke())
                dialog?.dismiss()
            }
            is Fail -> {
                Toast.makeText(requireContext(), getString(checkStatusApiRes(it.deleteTracking)), Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }



    private fun showDialog(tracking: Tracking?) {
        state = CURENT_TIME
        dialog = Dialog(requireContext())
        dialogBinding =
            FragmentBottomsheetTrackingBinding.inflate(requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
        dialog!!.setContentView(dialogBinding!!.root)
        dialog!!.show()

        if (tracking == null) {
            mViewModel.runTimmRealTime()
            dialogBinding!!.btnAccept.setOnClickListener {
                mViewModel.handle(TrackingViewAction.addTrackingViewAction(dialogBinding!!.edtContent.text.toString()))
                state = ADD_TRACKING
            }
        } else {
            dialogBinding!!.edtContent.setText(tracking.content)
            dialogBinding!!.tvTime.text = tracking.date!!.convertToStringFormat(StringUltis.dateIso8601Format, StringUltis.dateDayTimeFormat)
            dialogBinding!!.btnAccept.setOnClickListener {
                tracking.content = dialogBinding!!.edtContent.text.toString()
                mViewModel.handle(TrackingViewAction.updateTrackingViewAction(tracking))
                state = UPDATE_TRACKING
            }
        }

        dialog!!.setOnDismissListener {
            mViewModel.stopTimmRealTime()
        }
    }

    private fun showOptionMenu(view: View, tracking: Tracking) {
        val popUpMenu = PopupMenu(requireContext(), view)
        popUpMenu.inflate(R.menu.menu_item_tracking)
        popUpMenu.show()

        popUpMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.edit_item -> {
                    mViewModel.handleReturnShowDialogAdd(tracking)
                }
                R.id.delete_item -> {
                    state = DELETE_TRACKING
                    mViewModel.handle(TrackingViewAction.deleteTrackingViewAction(tracking))
                }
            }
            true
        }
    }


}