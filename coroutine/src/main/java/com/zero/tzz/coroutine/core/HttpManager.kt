package com.zero.tzz.coroutine.core

/**
 *
 * @author Zero_Tzz
 * @date 2019-12-30 14:09
 * @description HttpManager
 */
internal class HttpManager private constructor() {
    companion object {
        private var instance: HttpManager? = null
        fun getDefault(): HttpManager {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = HttpManager()
                    }
                }
            }
            return instance!!
        }
    }

    inline fun <reified T> get(url: String, noinline liveDataBlock: LiveDataDsl<T>.() -> Unit) {
        HttpProvider.getHttp().getViewModel()?.let { viewModel ->
            viewModel.launch({ HttpProvider.getHttp().getHttpService().get(url) }, liveDataBlock)
        }
    }
}