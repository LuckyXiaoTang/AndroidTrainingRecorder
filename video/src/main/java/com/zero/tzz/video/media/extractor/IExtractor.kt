package com.zero.tzz.video.media.extractor

import android.media.MediaFormat
import java.nio.ByteBuffer

/**
 * 音视频分离器定义
 * @author Zero_Tzz
 * @date 2019-11-26 11:53
 * @description IExtractor
 */
interface IExtractor {

    /** 获取音视频格式参数 */
    fun getFormat(): MediaFormat?

    /** 读取音视频数据 */
    fun readBuffer(buffer: ByteBuffer): Int

    /** 获取当前帧时间 */
    fun getCurrentTimestamp(): Long

    /** 获取帧类型 */
    fun getSampleFlags(): Int

    /** Seek到指定位置，并返回实际帧的时间戳 */
    fun seek(pos: Long): Long

    fun setStartPos(pos: Long)

    /** 停止读取数据 */
    fun stop()
}