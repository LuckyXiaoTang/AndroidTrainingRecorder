package com.zero.tzz.video.media.encoder

/**
 *
 * @author Zero_Tzz
 * @date 2020-01-08 13:40
 * @description IEncodeStateListener
 */
interface IEncodeStateListener {
    fun encodeFinish(encoder: BaseEncoder)

}