package com.oceantech.tracking.data.repository

import com.oceantech.tracking.data.model.Feedback
import com.oceantech.tracking.data.network.ReDengueLocationApi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class ReDengueRepository @Inject constructor(
    val api: ReDengueLocationApi
) {
    fun saveFeedback(feedback: Feedback):Observable<Feedback> = api.saveFeedback(feedback).subscribeOn(Schedulers.io())
}