package com.zero.tzz.coroutine.core

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

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

    @POST
    suspend fun post(@Url url: String, @Body messageJson: RequestBody): ResponseBody

    @GET
    suspend fun get(@Url url: String, @QueryMap map: Map<String, String> = HashMap()): ResponseBody
}