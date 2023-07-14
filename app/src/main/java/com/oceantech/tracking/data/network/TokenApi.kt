package com.oceantech.tracking.data.network

import io.reactivex.Observable
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TokenApi {
    @DELETE("oauth/logout")
    fun revokeToken():Observable<Unit>

    @POST("oauth/token/revokeById/{tokenId}")
    fun revokeToken(@Path("tokenId") tokenId:String):Observable<Unit>

    @GET("tokens")
    fun getTokens():Observable<List<String>>

    @GET("tokens/{clientId}")
    fun getTokens(@Path("clientId") clientId:String):Observable<List<String>>

    @POST("tokens/revokeRefreshToken/{tokenId}")
    fun revokeRefreshToken(@Path("tokenId") tokenId:String):Observable<String>
}