/*
 * Copyright 2019 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oceantech.tracking.core

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.Success
import com.oceantech.tracking.utils.DataSource
import com.oceantech.tracking.utils.PublishDataSource
import io.reactivex.Observable
import io.reactivex.Single

abstract class TrackingViewModel<S : MavericksState, VA : NimpeViewModelAction, VE : NimpeViewEvents>(
    initialState: S)
    : MavericksViewModel<S>(initialState) {

    interface Factory<S : MavericksState> {
        fun create(state: S): MavericksViewModel<S>
    }

    // Used to post transient events to the View
    protected val _viewEvents = PublishDataSource<VE>()
    val viewEvents: DataSource<VE> = _viewEvents

    /**
     * This method does the same thing as the execute function, but it doesn't subscribe to the stream
     * so you can use this in a switchMap or a flatMap
     */
    // False positive
    @Suppress("USELESS_CAST")
    fun <T> Single<T>.toAsync(stateReducer: S.(Async<T>) -> S): Single<Async<T>> {
        setState { stateReducer(Loading()) }
        return map { Success(it) as Async<T> }
                .onErrorReturn { Fail(it) }
                .doOnSuccess { setState { stateReducer(it) } }
    }

    /**
     * This method does the same thing as the execute function, but it doesn't subscribe to the stream
     * so you can use this in a switchMap or a flatMap
     */
    // False positive
    @Suppress("USELESS_CAST")
    fun <T> Observable<T>.toAsync(stateReducer: S.(Async<T>) -> S): Observable<Async<T>> {
        setState { stateReducer(Loading()) }
        return map { Success(it) as Async<T> }
                .onErrorReturn { Fail(it) }
                .doOnNext { setState { stateReducer(it) } }
    }

    abstract fun handle(action: VA)


}
