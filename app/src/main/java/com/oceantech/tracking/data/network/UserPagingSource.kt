package com.oceantech.tracking.data.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.model.UserFilter
import retrofit2.HttpException
import java.io.IOException
import com.oceantech.tracking.data.model.Page

class UserPagingSource(
    val api:UserApi
):PagingSource<Int,User>() {
    companion object {
        private const val INITIAL_PAGE = 1
    }

    override fun getRefreshKey(state: PagingState<Int, User>): Int? {
        return state.anchorPosition.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition!!)?.prevKey
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        return try {
            val nextPageNumber = params.key ?: INITIAL_PAGE
            val filter = UserFilter(nextPageNumber, 10)
            val response = api.searchByPage(filter)

            LoadResult.Page(
                data = response.content!!,
                prevKey = if (nextPageNumber == INITIAL_PAGE) null else nextPageNumber - 1,
                nextKey = if (response.content.isEmpty()) null else nextPageNumber + 1
            )
        }catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}