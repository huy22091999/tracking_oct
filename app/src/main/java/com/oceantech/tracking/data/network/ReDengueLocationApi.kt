package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.*
import io.reactivex.Observable
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface ReDengueLocationApi {
    @GET("public/app/getDegueLocation")
    fun getAllLocation():Call<ResponseBody>
    @POST("public/app/CountNotification")
    fun postCountNotification():Call<Response>
    @POST("api/dengue-location/searchByDto")
    fun getDengueLocation(@Body filter: DengueLocationFilter): Call<Page<DengueLocation>>
    @GET("api/patientInformation/listPatient")
    fun getListPatient(@Query("month") month:Int,@Query("year")year: Int): Call<List<Patient>>
    @GET("api/dengue-location-item/listDengueLocationItem")
    fun getListDengueLocation(@Query("month") month:Int,@Query("year")year: Int): Call<List<Vector>>
    @POST("public/app")
    fun saveFeedback(@Body feedback: Feedback) : Observable<Feedback>
}