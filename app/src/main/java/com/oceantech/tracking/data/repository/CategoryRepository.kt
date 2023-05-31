package com.oceantech.tracking.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.oceantech.tracking.data.model.*
import com.oceantech.tracking.data.network.CategoryApi
import com.oceantech.tracking.data.network.NewsPagingSource
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    val api: CategoryApi
) {
    fun getNews(id:String):Observable<News> = api.getNews(id).subscribeOn(Schedulers.io())
    fun getCategory(language: Int): Observable<Page<Category>> = api.getCategory(CategoryFilter(language,1,100)).subscribeOn(Schedulers.io())
    fun getNews(language:Int,category: Category): Flow<PagingData<News>> {
        return Pager(
            PagingConfig(
                enablePlaceholders = false,
                pageSize = 10,
                prefetchDistance = 5
            )
        ) {
            NewsPagingSource(api,language,category)
        }.flow
    }
    
}