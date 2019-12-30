package com.zero.tzz.coroutine.core

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap
import retrofit2.http.Url

/**
 *
 * @author Zero_Tzz
 * @date 2019-12-30 11:46
 * @description HttpService
 */
@PublishedApi
internal interface HttpService {
    @POST
    suspend fun post(@Url url: String, @QueryMap map: Map<String, String> = HashMap()): ResponseBody

    @GET
    suspend fun get(@Url url: String, @QueryMap map: Map<String, String> = HashMap()): ResponseBody
}