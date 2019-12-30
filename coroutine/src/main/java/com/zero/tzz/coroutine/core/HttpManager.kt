package com.zero.tzz.coroutine.core

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

/**
 *
 * @author Zero_Tzz
 * @date 2019-12-30 14:09
 * @description HttpManager
 */
class HttpManager private constructor() {

    companion object {
        private var mWeakCtx: WeakReference<Context>? = null
        @PublishedApi
        internal lateinit var mRetrofit: HttpService
        private val instance: HttpManager by lazy { HttpManager() }

        fun init(context: Context) {
            mWeakCtx = WeakReference(context)
            mRetrofit = HttpRetrofit(mWeakCtx!!.get()!!).createService(HttpService::class.java)
        }

        fun getDefault() = instance
    }

    fun <T> get(url: String, clazz: Class<T>, block: ((T) -> Unit)? = null) {
        GlobalScope.launch {
            val responseBody = mRetrofit.get(url)
            val data = Gson().fromJson(responseBody.string(), clazz)
            block?.invoke(data)
        }
    }

    inline fun <reified T> get(url: String, noinline block: ((T) -> Unit)? = null) {
        GlobalScope.launch {
            val responseBody = mRetrofit.get(url)
            val data = Gson().fromJson(responseBody.string(), T::class.java)
            block?.invoke(data)
        }
    }
}