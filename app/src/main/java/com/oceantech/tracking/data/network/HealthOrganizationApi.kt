package com.oceantech.tracking.data.network

import com.oceantech.tracking.data.model.*
import retrofit2.http.Body
import retrofit2.http.POST


interface HealthOrganizationApi {
    @POST("public/app/healthOrganization/searchByPage")
    suspend fun getHealthOrganization(@Body filter: HealthOrganizationFilter): Page<HealthOrganization>

}