package com.oceantech.tracking.ui.tracking

import android.view.View
import com.airbnb.mvrx.*
import com.oceantech.tracking.core.TrackingBaseViewModel
import com.oceantech.tracking.data.model.Tracking
import com.oceantech.tracking.data.repository.TrackingRepository
import com.oceantech.tracking.utils.StringUltis
import com.oceantech.tracking.utils.convertLongToStringFormat
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import timber.log.Timber

class TrackingViewModel @AssistedInject constructor(
    @Assisted state: TrackingViewState,
    private val repo: TrackingRepository
): TrackingBaseViewModel<TrackingViewState, TrackingViewAction, TrackingViewEvent>(state) {

    var isRunning = false

    override fun handle(action: TrackingViewAction) {
        when(action){
            is TrackingViewAction.getAllTrackings -> getAllTracking()
            is TrackingViewAction.addTrackingViewAction -> addTracking(action.content)
            is TrackingViewAction.updateTrackingViewAction -> updateTracking(action.tracking)
            is TrackingViewAction.deleteTrackingViewAction -> deleteTracking(action.tracking)
        }
    }

    private fun getAllTracking(){
        setState { copy(getTrackings = Loading()) }
        repo.getAllTracking().execute {
            copy(getTrackings = it)
        }
    }

    private fun addTracking(content : String){
        withState { it ->
            val tracking = Tracking(null, content, it.currentTime.invoke()!!.convertLongToStringFormat(StringUltis.dateIso8601Format), null)
            setState { copy(addTracking = Loading()) }
            repo.addTracking(tracking).execute {
                copy(addTracking = it)
            }
        }
    }

    private fun updateTracking(tracking: Tracking) {
        setState { copy(updateTracking = Loading()) }
        repo.updateTracking(tracking.id!!, tracking).execute {
            copy(updateTracking = it)
        }
    }

    private fun deleteTracking(tracking: Tracking) {
        setState { copy(deleteTracking = Loading()) }
        repo.deleteTracking(tracking.id!!).execute {
            copy(deleteTracking = it)
        }
    }

    fun runTimmRealTime(){
        isRunning = true
        withState {
            Thread {
                while (isRunning){
                    var long = System.currentTimeMillis()
                    setState { copy(currentTime = Success(long)) }
                    Thread.sleep(1000)
                }
            }.start()
        }
    }

    fun stopTimmRealTime(){
        isRunning = false
    }

    fun handleReturnShowDialogAdd(tracking: Tracking?) {
        _viewEvents.post(TrackingViewEvent.ReturnShowDialogViewEvent(tracking))
    }

    fun handleReturnShowOptionMenu(view: View, tracking: Tracking) {
        _viewEvents.post(TrackingViewEvent.ReturnShowOptionMenuViewEvent(view, tracking))
    }

    @AssistedFactory
    interface Factory {
        fun create(initialState: TrackingViewState): TrackingViewModel
    }

    companion object : MvRxViewModelFactory<TrackingViewModel, TrackingViewState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: TrackingViewState
        ): TrackingViewModel? {
            val fatory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }
            return fatory?.create(state)
                ?: error("You should let your activity/fragment implements Factory interface")
        }
    }
}