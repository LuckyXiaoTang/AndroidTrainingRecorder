package com.zero.tzz.video.media

/**
 * 解码状态回调接口
 * @author Zero_Tzz
 * @date 2019-11-26 11:59
 * @description IDecodeStateListener
 */
interface IDecodeStateListener {
    fun decodePrepare(decoder: BaseDecoder?)
    fun decodePause(decoder: BaseDecoder?)
    fun decodeDecoding(decoder: BaseDecoder?)
    fun decodeError(decoder: BaseDecoder?, error: String)
    fun decodeFinished(decoder: BaseDecoder?)
    fun decoderDestroy(decoder: BaseDecoder?)

}