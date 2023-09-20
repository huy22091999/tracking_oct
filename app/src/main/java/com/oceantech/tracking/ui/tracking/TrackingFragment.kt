package com.oceantech.tracking.ui.tracking

import android.graphics.Canvas
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.databinding.FragmentTrackingBinding
import com.oceantech.tracking.utils.DialogUtil
import com.oceantech.tracking.utils.checkStatusApiRes
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

@Suppress("DEPRECATION")
class TrackingFragment : TrackingBaseFragment<FragmentTrackingBinding>() {

    val trackingViewModel: TrackingViewModel by activityViewModel()
    private var mlistTracking: MutableList<Tracking> = mutableListOf()

    private lateinit var rvAdapter: TrackingAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTrackingBinding {
        return FragmentTrackingBinding.inflate(inflater, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpCalender()
        setUpRcv()
        clickUi()
        trackingViewModel.handle(TrackingViewAction.getTrackingAction)
    }

    private fun clickUi() {
        views.btnCheckin.setOnClickListener {
            views.btnCheckin.visibility = View.GONE
            views.layoutTracking.visibility = View.VISIBLE
        }
        views.btnSendFeedback.setOnClickListener {
            views.btnCheckin.visibility = View.VISIBLE
            views.layoutTracking.visibility = View.GONE
        }
    }

    private fun setUpRcv() {
        rvAdapter = TrackingAdapter(requireActivity(), mlistTracking)
        views.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        views.recyclerView.adapter = rvAdapter

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (direction == ItemTouchHelper.LEFT) {
                    // Gọi phương thức xóa item từ Adapter
                    val position = viewHolder.adapterPosition
                    DialogUtil.showAlertDialogDelete(
                        requireActivity(),
                        getString(R.string.success),
                        getString(R.string.checked), { rvAdapter.removeItem(position) }
                    )

                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView = viewHolder.itemView
                    val icon = ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.icon_delete
                    ) // Thay R.drawable.ic_delete bằng ID của biểu tượng xoá của bạn
                    val iconMargin = (itemView.height - icon?.intrinsicHeight!!) / 2

                    // Giới hạn khoảng vuốt
                    val limitedDx = if (dX < -iconMargin) -iconMargin else dX

                    val iconLeft =
                        itemView.right - iconMargin - icon.intrinsicWidth + limitedDx.toInt()
                    val iconTop = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
                    val iconRight = itemView.right - iconMargin + limitedDx.toInt()
                    val iconBottom = iconTop + icon.intrinsicHeight

                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    icon.draw(c)
                }
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        })

        itemTouchHelper.attachToRecyclerView(views.recyclerView)


    }

    private fun setUpCalender() {
        views.calenderEvent.state().edit().setCalendarDisplayMode(CalendarMode.WEEKS).commit()
        val currentDate = CalendarDay.today()
        val selectedDateDecorator = object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay?): Boolean {
                return day == currentDate
            }

            override fun decorate(view: DayViewFacade?) {
                view?.addSpan(ForegroundColorSpan(resources.getColor(R.color.red)))
            }
        }
        views.calenderEvent.addDecorators(selectedDateDecorator)
    }

    override fun invalidate(): Unit = withState(trackingViewModel) {
        when (it.listTracking) {
            is Success -> {
                mlistTracking.addAll(it.listTracking.invoke())
                rvAdapter.notifyDataSetChanged()
            }

            is Fail -> {
                Toast.makeText(
                    requireContext(),
                    getString(checkStatusApiRes(it.listTracking)),
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                false
            }
        }
    }

}