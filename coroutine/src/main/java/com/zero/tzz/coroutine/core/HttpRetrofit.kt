package com.zero.tzz.coroutine.core

import android.content.Context
import android.os.Environment
import com.zero.tzz.coroutine.BuildConfig
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit


/**
 *
 * @author Zero_Tzz
 * @date 2019-12-30 13:55
 * @description HttpRetrofit
 */
class HttpRetrofit(private val context: Context) {

    companion object {
        private const val TIME_OUT = 30
    }

    private val client: OkHttpClient
        get() {
            val builder = OkHttpClient.Builder()
            val logging = HttpLoggingInterceptor()
            logging.level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
            //create a cache
            val cacheDir = Environment.getExternalStorageDirectory()
            val cache = Cache(cacheDir, 20 * 1024 * 1024)
            builder.cache(cache)
                .addInterceptor(logging)
                .addInterceptor(CacheIterceptor(context))
                .readTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
                .connectTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
            return builder.build()
        }

    fun <T> createService(clazz: Class<T>): T {
        return Retrofit.Builder()
            .client(client)
            .baseUrl("http://xxxxxx.xxx")
            .build().create(clazz)
    }
}