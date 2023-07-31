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

package com.oceantech.tracking.utils

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.model.PageSearch
import com.oceantech.tracking.data.network.UserApi
import com.oceantech.tracking.data.repository.UserRepository
import com.oceantech.tracking.ui.home.HomeViewState
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.rx2.asFlow
import retrofit2.HttpException

interface DataSource<T> {
    fun observe(): Flow<T>
}

interface MutableDataSource<T> : DataSource<T> {
    fun post(value: T)
}

/**
 * This datasource emits the most recent value it has observed and all subsequent observed values to each subscriber.
 */
open class BehaviorDataSource<T : Any>(private val defaultValue: T? = null) : MutableDataSource<T> {

    private val behaviorRelay = createRelay()

    val currentValue: T?
        get() = behaviorRelay.value

    override fun observe(): Flow<T> {
        return (behaviorRelay.hide() as ObservableSource<T>).asFlow().flowOn(Dispatchers.Main)
    }

    override fun post(value: T) {
        behaviorRelay.accept(value)
    }

    private fun createRelay(): BehaviorRelay<T> {
        return if (defaultValue == null) {
            BehaviorRelay.create()
        } else {
            BehaviorRelay.createDefault(defaultValue)
        }
    }
}

/**
 * This datasource only emits all subsequent observed values to each subscriber.
 */
open class PublishDataSource<T : Any> : MutableDataSource<T> {

    private val publishRelay = PublishRelay.create<T>()

    override fun observe(): Flow<T> {
        return (publishRelay.hide() as ObservableSource<T>).asFlow().flowOn(Dispatchers.Main)
    }

    override fun post(value: T) {
        publishRelay.accept(value)
    }

}

class UserPagingSource : PagingSource<Int, User> {
    private lateinit var api: UserApi
    private lateinit var repository: UserRepository

    constructor(api: UserApi) {
        this.api = api
    }

    constructor(repository: UserRepository) {
        this.repository = repository
    }

    override fun getRefreshKey(state: PagingState<Int, User>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        return try {
            val nextPageNumber = params.key ?: 1
//            val pageContent = api.searchByPage(
//                PageSearch(
//                    pageIndex = nextPageNumber,
//                    size = 5
//                )
//            )
            val pageContent = repository.searchByPage(
                PageSearch(
                    pageIndex = nextPageNumber,
                    size = 5
                )
            ).first()
            val users = pageContent.content
            LoadResult.Page(
                data = users,
                if (pageContent.first) null else nextPageNumber - 1,
                if (pageContent.last) null else nextPageNumber + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }

    }


}