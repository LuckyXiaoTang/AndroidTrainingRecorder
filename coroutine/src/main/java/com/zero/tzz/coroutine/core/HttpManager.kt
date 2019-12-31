package com.zero.tzz.coroutine.core

import okhttp3.MultipartBody


/**
 *
 * @author Zero_Tzz
 * @date 2019-12-30 14:09
 * @description HttpManager
 */
object HttpManager {

    inline fun <reified T> get(
        url: String,
        map: Map<String, String> = HashMap(),
        noinline liveDataBlock: LiveDataDsl<T>.() -> Unit
    ) {
        HttpProvider.getHttp().getViewModel()?.let { viewModel ->
            viewModel.launch(
                { HttpProvider.getHttp().getHttpService().get(url, map) },
                liveDataBlock
            )
        }
    }

    inline fun <reified T> post(
        url: String,
        map: Map<String, String> = HashMap(),
        noinline liveDataBlock: LiveDataDsl<T>.() -> Unit
    ) {
        HttpProvider.getHttp().getViewModel()?.let { viewModel ->
            viewModel.launch(
                { HttpProvider.getHttp().getHttpService().post(url, map) },
                liveDataBlock
            )
        }
    }

    inline fun <reified T> postJson(
        url: String,
        param: String,
        json: String,
        noinline liveDataBlock: LiveDataDsl<T>.() -> Unit
    ) {
        HttpProvider.getHttp().getViewModel()?.let { viewModel ->
            viewModel.launch(
                {
                    val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
                    builder.addFormDataPart(param, json)
                    HttpProvider.getHttp().getHttpService().post(url, builder.build())
                },
                liveDataBlock
            )
        }
    }
}