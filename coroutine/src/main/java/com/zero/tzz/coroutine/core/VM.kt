package com.zero.tzz.coroutine.core

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

/**
 * Created by Tzz on 2019/12/30.
 */
@PublishedApi
internal class VM : ViewModel(), LifecycleObserver {

    val lifecycles: MutableList<LifecycleOwner> = mutableListOf()
    fun addLifecycle(lifecycle: LifecycleOwner) {
        lifecycles.add(lifecycle)
    }

    fun removeLifecycle(lifecycle: LifecycleOwner) {
        lifecycles.remove(lifecycle)
    }

    fun launchUI(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch { block() }
    }

    inline fun <reified T> launch(
        noinline block: suspend CoroutineScope.() -> ResponseBody,
        noinline liveDataBlock: LiveDataDsl<T>.() -> Unit
    ) {
        launchUI {
            coroutineScope {
                val liveDataDsl = LiveDataDsl<T>(lifecycles)
                liveDataDsl.liveDataBlock()
                try {
                    val responseBody = block()
                    val data = Gson().fromJson(responseBody.string(), T::class.java)
                    liveDataDsl.successLiveData.value = data
                } catch (e: Exception) {
                    liveDataDsl.errorLiveData.value = ApiException(e).parser()
                }
            }
        }
    }
}