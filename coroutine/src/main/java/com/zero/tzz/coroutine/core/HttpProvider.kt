package com.zero.tzz.coroutine.core

import android.app.Application
import java.lang.ref.WeakReference

/**
 * Created by Tzz on 2019/12/30.
 */
internal object HttpProvider {
    private lateinit var mApp: Application
    private var mWeakHttp: WeakReference<HttpTest>? = null
    fun init(application: Application) {
        mApp = application
        mWeakHttp = WeakReference(HttpTest())
    }

    fun get(): Application = mApp
    fun getHttp(): HttpTest {
        if (mWeakHttp == null || mWeakHttp!!.get() == null) {
            mWeakHttp = WeakReference(HttpTest())
        }
        return mWeakHttp!!.get()!!
    }

    fun clearHttp() {
        mWeakHttp?.clear()
        mWeakHttp = null
    }
}