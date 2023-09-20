package com.oceantech.tracking.ui.tracking

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import com.airbnb.mvrx.*
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.core.TrackingClickItem
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.databinding.FragmentTrackingBinding
import com.oceantech.tracking.ui.home.HomeViewModel
import com.oceantech.tracking.ui.profile.InfoActivity
import com.oceantech.tracking.utils.*


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
    private val homeViewModel: HomeViewModel by activityViewModel()
    var adapter: TrackingAdapter? = null

    var dialog: Dialog? = null


    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTrackingBinding {
        return FragmentTrackingBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        state = GET_TRACKING
        mViewModel.handle(TrackingViewAction.getAllTrackings)

        setUpRcv()
        onListenner()

        homeViewModel.subscribe(requireActivity()){
            mViewModel.curentUser = it.userCurrent.invoke()
        }
    }

    private fun onListenner() {

        mViewModel.observeViewEvents {
            handlEvent(it)
        }

        views.swipeLayout.setOnRefreshListener {
            state = GET_TRACKING
            mViewModel.handle(TrackingViewAction.getAllTrackings)
        }
    }

    private fun handlEvent(it: TrackingViewEvent) {

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setUpRcv() {
        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(requireActivity().resources.getDrawable(R.drawable.custom_divider_item))
        val linearLayoutManager = LinearLayoutManager(requireContext())

        adapter = TrackingAdapter(object : TrackingClickItem() {
            override fun onItemTrackingAddClickListenner() {
                super.onItemTrackingAddClickListenner()
                showDialog(null)
            }

            override fun onItemTrackingOptionMenuClickListenner(view: View, tracking: Tracking) {
                super.onItemTrackingOptionMenuClickListenner(view, tracking)
                showOptionMenu(view, tracking)
            }

            override fun onItemPosition(position: Int) {
                super.onItemPosition(position)
                    scrollRcv(linearLayoutManager, position)
            }

            override fun onItemAvatarClickListennner() {
                super.onItemAvatarClickListennner()
                requireActivity().startActivityAnim(Intent(requireActivity(), InfoActivity::class.java))
            }
        })


        views.rcv.adapter = adapter
        views.rcv.layoutManager = linearLayoutManager
        views.rcv.addItemDecoration(dividerItemDecoration)

        views.rcv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (linearLayoutManager.findFirstVisibleItemPosition() == 0) {
                    // RecyclerView ở đầu danh sách
                    mViewModel.handle(TrackingViewAction.rcvScrollDown)
                }

                if (dy !in -50..10) {
                    if (dy > 0) {
                        mViewModel.handle(TrackingViewAction.rcvScrollUp)
                    } else {
                        mViewModel.handle(TrackingViewAction.rcvScrollDown)
                    }
                }


            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> println("The RecyclerView is not scrolling")
                    RecyclerView.SCROLL_STATE_DRAGGING -> println("Scrolling now")
                    RecyclerView.SCROLL_STATE_SETTLING -> println("Scroll Settling")
                }
            }
        })
    }

    private fun scrollRcv(linearLayoutManager: LinearLayoutManager, position: Int) {
        val smoothScroller: SmoothScroller = object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_ANY // cuộn item đến bất kỳ ds
            }
        }
        smoothScroller.targetPosition = position;
        linearLayoutManager.startSmoothScroll(smoothScroller);
    }

    override fun invalidate(): Unit = withState(mViewModel) {
        when (state) {
            GET_TRACKING -> handleGetAddTrackings(it)
            ADD_TRACKING -> handleAddTracking(it)
            UPDATE_TRACKING -> handleUpdateTracking(it)
            DELETE_TRACKING -> handleDeleteTracking(it)
            else -> {}
        }
    }

    private fun handleGetAddTrackings(it: TrackingViewState) {
        when (it.getTrackings) {
            is Success -> {
                adapter!!.setAllData(it.getTrackings.invoke())
                views.swipeLayout.isRefreshing = false
                state = DEFAULT_STATE
            }
            is Fail -> {
                Toast.makeText(
                    requireContext(),
                    getString(checkStatusApiRes(it.getTrackings)),
                    Toast.LENGTH_SHORT
                ).show()
                state = DEFAULT_STATE
            }
            else -> {}
        }
    }

    private fun handleAddTracking(it: TrackingViewState) {
        when (it.addTracking) {
            is Success -> {
                val tracking = it.addTracking.invoke()
                adapter?.addItemData(tracking)
                dialog?.dismiss()
                showSnackbar(views.root, getString(R.string.success), getString(R.string.view), R.color.text_title1){
                    adapter?.findItemPositoon(tracking)
                }
                state = DEFAULT_STATE
            }
            is Fail -> {
                showSnackbar(views.root, getString(R.string.failed), null, R.color.red){}
                state = DEFAULT_STATE
            }
            else -> {}
        }
    }

    private fun handleUpdateTracking(it: TrackingViewState) {
        when (it.updateTracking) {
            is Success -> {
                val tracking = it.updateTracking.invoke()
                adapter?.updateItemData(tracking)
                dialog?.dismiss()
                showSnackbar(views.root, getString(R.string.success), getString(R.string.view), R.color.text_title1){
                    adapter?.findItemPositoon(tracking)
                }
                state = DEFAULT_STATE
            }
            is Fail -> {
                showSnackbar(views.root, getString(R.string.failed), null, R.color.red){}
                state = DEFAULT_STATE
            }
            else -> {}
        }
    }

    private fun handleDeleteTracking(it: TrackingViewState) {
        when (it.deleteTracking) {
            is Success -> {
                adapter?.deleteItemData(it.deleteTracking.invoke())
                dialog?.dismiss()
                showSnackbar(views.root, getString(R.string.success), null, R.color.text_title1){}
                state = DEFAULT_STATE
            }
            is Fail -> {
                showSnackbar(views.root, getString(R.string.failed), null, R.color.red){}
                state = DEFAULT_STATE
            }
            else -> {}
        }
    }

    @SuppressLint("LogNotTimber")
    private fun showDialog(tracking: Tracking?) {
        val dialog: TrackingBottomSheetFragment =
            TrackingBottomSheetFragment.newInstance(tracking, tracking?.user ?: mViewModel.curentUser) {
                if (tracking == null) {
                    mViewModel.handle(TrackingViewAction.addTrackingViewAction(it))
                    state = ADD_TRACKING
                } else {
                    tracking.content = it
                    mViewModel.handle(TrackingViewAction.updateTrackingViewAction(tracking))
                    state = UPDATE_TRACKING
                }
            }
        dialog.show(requireActivity().supportFragmentManager, "")


    }

    private fun showOptionMenu(view: View, tracking: Tracking) {
        val popUpMenu = PopupMenu(requireContext(), view)
        popUpMenu.inflate(R.menu.menu_item_tracking)
        popUpMenu.show()

        popUpMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.edit_item -> {
                    showDialog(tracking)
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