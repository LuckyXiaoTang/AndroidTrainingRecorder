package com.zero.tzz.coroutine.core

import android.app.Application
import java.lang.ref.WeakReference

/**
 * Created by Tzz on 2019/12/30.
 */
@PublishedApi
internal object HttpProvider {
    private lateinit var mApp: Application
    private var mWeakHttp: WeakReference<HttpLifecycle>? = null
    fun init(application: Application) {
        mApp = application
        mWeakHttp = WeakReference(HttpLifecycle())
    }

    fun get(): Application = mApp
    fun getHttp(): HttpLifecycle {
        if (mWeakHttp == null || mWeakHttp!!.get() == null) {
            mWeakHttp = WeakReference(HttpLifecycle())
        }
        return mWeakHttp!!.get()!!
    }
}