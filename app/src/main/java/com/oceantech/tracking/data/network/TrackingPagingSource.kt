package com.oceantech.tracking.data.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.oceantech.tracking.data.model.Tracking
import retrofit2.HttpException
import java.io.IOException

//class TrackingPagingSource(
//    private val api: TrackingApi
//):PagingSource<Int, Tracking>() {
//    companion object {
//        private const val INITIAL_PAGE = 1
//    }
//    override fun getRefreshKey(state: PagingState<Int, Tracking>): Int? {
//        return state.anchorPosition?.let { anchorPosition ->
//            // This loads starting from previous page, but since PagingConfig.initialLoadSize spans
//            // multiple pages, the initial load will still load items centered around
//            // anchorPosition. This also prevents needing to immediately launch prepend due to
//            // prefetchDistance.
//            state.closestPageToPosition(anchorPosition)?.prevKey
//        }
//    }
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Tracking> {
//        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Tracking> {
//            try {
//                val nextPage = params.key ?: 1 // Trang đầu tiên là 1
//                val response = api.getData(nextPage)
//                val data = response.yourDataList
//                val prevPage = if (nextPage == 1) null else nextPage - 1
//                val nextPage = nextPage + 1
//                return LoadResult.Page(data, prevPage, nextPage)
//            } catch (e: Exception) {
//                return LoadResult.Error(e)
//            }
//        }
//    }
//}