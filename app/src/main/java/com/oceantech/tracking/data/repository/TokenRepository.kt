package com.oceantech.tracking.data.repository

import com.oceantech.tracking.data.network.TokenApi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import retrofit2.http.Path
import javax.inject.Inject

class TokenRepository @Inject constructor(
    private val api:TokenApi
) {
    fun revokeToken():Observable<Unit> = api.revokeToken().subscribeOn(Schedulers.io())

    fun revokeToken(tokenId:String):Observable<Unit> = api.revokeToken(tokenId).subscribeOn(Schedulers.io())

    fun getTokens():Observable<List<String>> = api.getTokens().subscribeOn(Schedulers.io())

    fun getTokens(clientId:String):Observable<List<String>> = api.getTokens(clientId).subscribeOn(Schedulers.io())

    fun revokeRefreshToken(tokenId:String):Observable<String> = api.revokeRefreshToken(tokenId).subscribeOn(Schedulers.io())
}