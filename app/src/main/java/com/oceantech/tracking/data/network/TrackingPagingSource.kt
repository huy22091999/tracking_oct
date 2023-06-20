package com.oceantech.tracking.data.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.oceantech.tracking.data.model.Tracking

class TrackingPagingSource(
    private val trackings:List<Tracking>
):PagingSource<Int, Tracking>() {
    companion object{
        private const val INITIAL_PAGE_NUMBER = 1
        private const val PAGE_SIZE = 20
        private var CURRENT_PAGE_NUMBER = INITIAL_PAGE_NUMBER
    }
    override fun getRefreshKey(state: PagingState<Int, Tracking>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Tracking> {
        return try{
            val pageNumber = params.key ?: INITIAL_PAGE_NUMBER

            val nextPageNumber = CURRENT_PAGE_NUMBER +1
            CURRENT_PAGE_NUMBER = nextPageNumber
            LoadResult.Page(
                data = trackings,
                prevKey = null,
                nextKey = nextPageNumber
            )
        } catch (e:Exception){
            LoadResult.Error(e)
        }
    }
}