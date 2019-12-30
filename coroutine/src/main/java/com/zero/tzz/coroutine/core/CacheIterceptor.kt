package com.zero.tzz.coroutine.core

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

/**
 *
 * @author Zero_Tzz
 * @date 2019-12-30 13:07
 * @description CacheIterceptor
 */
internal class CacheIterceptor(
    private val context: Context,
    // 一周
    private val maxStale: Int = 60 * 60 * 24 * 7,
    // 5分钟
    private val maxAge: Int = 5 * 60
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (!isNetworkAvailable()) {
            request = request.newBuilder()
                .cacheControl(FORCE_CACHE)
                .build()
        }
        val response = chain.proceed(request)
        if (isNetworkAvailable()) {
            response.newBuilder()
                .removeHeader("pragma")
                .header("Cache-Control", "public, only-if-cached, max-age=$maxAge")
                .build()
        } else {
            response.newBuilder()
                .removeHeader("pragma")
                .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                .build()
        }
        return response
    }

    @SuppressLint("MissingPermission")
    private fun isNetworkAvailable(): Boolean {
        val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val permission = context.packageManager.checkPermission(
            Manifest.permission.ACCESS_NETWORK_STATE,
            context.packageName
        )
        if (PackageManager.PERMISSION_GRANTED == permission) {
            val activeNetworkInfo = cm.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isAvailable
        }
        return false
    }

    private val FORCE_CACHE = CacheControl.Builder()
        .onlyIfCached()
        .maxStale(maxStale, TimeUnit.SECONDS)
        .build()
}