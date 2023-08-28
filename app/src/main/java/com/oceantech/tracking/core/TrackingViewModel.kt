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
import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Success
import com.oceantech.tracking.utils.DataSource
import com.oceantech.tracking.utils.PublishDataSource
import io.reactivex.Observable
import io.reactivex.Single
// Lớp abstract TrackingViewModel sử dụng trong kiến trúc MvRx (Model-View-React).
abstract class TrackingViewModel<S : MvRxState, VA : NimpeViewModelAction, VE : NimpeViewEvents>(initialState: S)
    : BaseMvRxViewModel<S>(initialState, false) {

    // Interface Factory cho phép tạo ViewModel với trạng thái S.
    interface Factory<S : MvRxState> {
        fun create(state: S): BaseMvRxViewModel<S>
    }

    // Used to post transient events to the View
    // _viewEvents là một PublishDataSource dùng để gửi các sự kiện tạm thời đến View.
    protected val _viewEvents = PublishDataSource<VE>()
    // viewEvents là một DataSource (nguồn dữ liệu) chứa các sự kiện VE sẽ gửi đến View để cập nhật giao diện.
    val viewEvents: DataSource<VE> = _viewEvents

    /**
     * Phương thức này thực hiện việc tương tự như hàm execute, nhưng không subscribe vào luồng dữ liệu,
     * cho phép sử dụng trong switchMap hoặc flatMap.
     */
    // False positive
    @Suppress("USELESS_CAST", "NULLABLE_TYPE_PARAMETER_AGAINST_NOT_NULL_TYPE_PARAMETER")
    fun <T> Single<T>.toAsync(stateReducer: S.(Async<T>) -> S): Single<Async<T>> {
        // Đặt trạng thái là Loading() trước khi bắt đầu thực hiện thao tác bất đồng bộ.
        setState { stateReducer(Loading()) }
        // Chuyển đổi kết quả thành Success nếu thành công hoặc Fail nếu có lỗi,
        // và đặt lại trạng thái của ViewModel bằng kết quả mới.
        return map { Success(it) as Async<T> }
            .onErrorReturn { Fail(it) }
            .doOnSuccess { setState { stateReducer(it) } }
    }

    /**
     * Phương thức này thực hiện việc tương tự như hàm execute, nhưng không subscribe vào luồng dữ liệu,
     * cho phép sử dụng trong switchMap hoặc flatMap.
     */
    // False positive
    @Suppress("USELESS_CAST", "NULLABLE_TYPE_PARAMETER_AGAINST_NOT_NULL_TYPE_PARAMETER")
    fun <T> Observable<T>.toAsync(stateReducer: S.(Async<T>) -> S): Observable<Async<T>> {
        // Đặt trạng thái là Loading() trước khi bắt đầu thực hiện thao tác bất đồng bộ.
        setState { stateReducer(Loading()) }
        // Chuyển đổi kết quả thành Success nếu thành công hoặc Fail nếu có lỗi,
        // và đặt lại trạng thái của ViewModel bằng kết quả mới.
        return map { Success(it) as Async<T> }
            .onErrorReturn { Fail(it) }
            .doOnNext { setState { stateReducer(it) } }
    }

    // Phương thức abstract handle(action: VA) để xử lý các hành động VA (sự kiện từ View).
    abstract fun handle(action: VA)
}
