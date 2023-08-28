package com.oceantech.tracking.data.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.data.model.UserFilter
import retrofit2.HttpException
import java.io.IOException

class UsersPagingSource(val api: UserApi): PagingSource<Int, User>() {
    companion object {
        private const val INITIAL_PAGE = 1
    }
    override fun getRefreshKey(state: PagingState<Int, User>): Int? {
        return state.anchorPosition.let {
            state.closestPageToPosition(it!!)?.prevKey
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        return try {
            // Start refresh at page 1 if undefined.
            val nextPage=params.key?: INITIAL_PAGE
            val filter= UserFilter(nextPage,10)
            val response=api.getAllUser(filter)
             LoadResult.Page(
                data = response.content!!,
                prevKey = if(nextPage== INITIAL_PAGE) null else nextPage-1,
                nextKey = if(response.content.isEmpty()) null else nextPage+1
            )

        }catch (e:IOException){
            LoadResult.Error(e)
        } catch (e: HttpException){
            LoadResult.Error(e)
        }

    }
}