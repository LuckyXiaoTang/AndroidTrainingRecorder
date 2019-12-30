package com.zero.tzz.coroutine.core

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

/**
 * Created by Tzz on 2019/12/30.
 */
class LiveDataDsl<T>(private val lifecycles: MutableList<LifecycleOwner>?) {
    internal val successLiveData = MutableLiveData<T>()
    internal val errorLiveData = MutableLiveData<String>()
    fun success(block: (T) -> Unit) {
        lifecycles?.forEach { successLiveData.observe(it, Observer(block)) }
    }

    fun error(block: (String) -> Unit) {
        lifecycles?.forEach { errorLiveData.observe(it, Observer(block)) }
    }
}