package com.oceantech.tracking.ui.users

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.oceantech.tracking.data.model.Pageable
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.network.UserApi
import retrofit2.HttpException
import java.io.IOException

class UserPagingSource(private val userApi: UserApi) : PagingSource<Int, User>() {

    companion object {
        const val USER_START_PAGE_INDEX = 1;
        const val NETWORK_PAGE_SIZE = 10;
    }


    override fun getRefreshKey(state: PagingState<Int, User>): Int? {
        return state.anchorPosition.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition!!)?.prevKey
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        return try {

            val position = params.key ?: USER_START_PAGE_INDEX
            val users = userApi.getUserByPage(Pageable(position, params.loadSize))

            LoadResult.Page(
                data = users.content!!,
                prevKey = if (position == USER_START_PAGE_INDEX) null else position - 1,
                nextKey = if (users.content.isEmpty()) null else position + 1
            )
        }catch (e : IOException){
            LoadResult.Error(e)
        }catch (e : Exception){
            LoadResult.Error(e)
        }catch (e : HttpException){
            LoadResult.Error(e)
        }
    }


}