package com.zero.tzz.video.media

import android.media.MediaCodec
import java.nio.ByteBuffer

/**
 *
 * @author Zero_Tzz
 * @date 2020-01-08 13:35
 * @description Frame
 */
class Frame {
    /** 未编码数据 */
    var buffer: ByteBuffer? = null
    /** 未编码数据信息 */
    var bufferInfo = MediaCodec.BufferInfo()
        private set

    fun setBufferInfo(info: MediaCodec.BufferInfo) {
        bufferInfo.set(info.offset, info.size, info.presentationTimeUs, info.flags)
    }
}