package com.zero.tzz.video.media

import android.media.MediaFormat

/**
 * 解码器定义
 * @author Zero_Tzz
 * @date 2019-11-26 11:37
 * @description IDecoder
 */
interface IDecoder : Runnable {

    /** 暂停解码 */
    fun pause()

    /** 继续解码 */
    fun resume()

    /** 停止解码 */
    fun stop()

    /** 是否正在解码 */
    fun isDecoding(): Boolean

    /** 是否正在快进/退 */
    fun isSeeking(): Boolean

    /** 是否已停止 */
    fun isStop(): Boolean

    /** 获取视频宽 */
    fun getWidth(): Int

    /** 获取视频高 */
    fun getHeight(): Int

    /** 获取视频长度 */
    fun getDuration(): Long

    /** 获取当前帧时间戳 */
    fun getCurTimestamp(): Long

    /** 获取视频旋转角度 */
    fun getRotateAngle(): Int

    /** 获取音视频对应的格式参数 */
    fun getMediaFormat(): MediaFormat?

    /** 获取音视频对应的媒体轨道 */
    fun getTrack(): Int

    /** 获取解码的文件路径 */
    fun getFilePath(): String

    /** 设置状态监听 */
    fun setDecodeListener(listener:IDecodeStateListener?)
}