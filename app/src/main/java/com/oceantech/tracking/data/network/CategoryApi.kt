package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.*
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface CategoryApi {
    @GET("api/{id}")
    fun getNews(@Path("id")id:String): Observable<News>
    @POST("public/app/searchByPage")
    fun getCategory(@Body filter: CategoryFilter): Observable<Page<Category>>
    @POST("public/app/searchByDto")
    suspend fun getNews(@Body filter: NewsFilter): Page<News>

}