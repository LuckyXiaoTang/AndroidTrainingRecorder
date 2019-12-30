package com.zero.tzz.coroutine.core

import retrofit2.HttpException
import java.net.ConnectException
import java.net.UnknownHostException

/**
 * Created by Tzz on 2019/12/30.
 */
@PublishedApi
internal class ApiException(val e: Exception) {

    companion object {
        //对应HTTP的状态码
        private const val UNAUTHORIZED = 401
        private const val FORBIDDEN = 403
        private const val NOT_FOUND = 404
        private const val REQUEST_TIMEOUT = 408
        private const val INTERNAL_SERVER_ERROR = 500
        private const val BAD_GATEWAY = 502
        private const val SERVICE_UNAVAILABLE = 503
        private const val GATEWAY_TIMEOUT = 504
    }

    fun parser(): String {
        return when (e) {
            is HttpException -> {
                "${e.code()} ${e.message()}"
            }
            is UnknownHostException -> "UnknownHostException"
            is ConnectException -> "ConnectException"
            else -> e.message!!
        }
    }
}