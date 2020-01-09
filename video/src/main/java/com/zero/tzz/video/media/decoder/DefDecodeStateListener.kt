package com.zero.tzz.video.media.decoder

import com.zero.tzz.video.media.Frame


/**
 * 默认解码状态监听器
 *
 * @author Chen Xiaoping (562818444@qq.com)
 * @since LearningVideo
 * @version LearningVideo
 *
 */
interface DefDecodeStateListener: IDecodeStateListener {
   override fun decodePrepare(decoder: BaseDecoder?){}
   override fun decodePause(decoder: BaseDecoder?){}
   override fun decodeDecoding(decoder: BaseDecoder?){}
   override fun decodeError(decoder: BaseDecoder?, error: String){}
   override fun decodeFinished(decoder: BaseDecoder?){}
   override fun decoderDestroy(decoder: BaseDecoder?){}
   override fun decodeOneFrame(decodeJob: BaseDecoder?, frame: Frame){}
}