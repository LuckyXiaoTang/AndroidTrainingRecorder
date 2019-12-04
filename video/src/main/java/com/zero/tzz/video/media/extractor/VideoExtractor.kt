package com.zero.tzz.video.media.extractor

import android.media.MediaFormat
import com.zero.tzz.video.media.IExtractor
import java.nio.ByteBuffer

/**
 *
 * @author Zero_Tzz
 * @date 2019-11-26 17:56
 * @description VideoDecoder
 */
class VideoExtractor(mFilePath: String) : IExtractor {

    private val mExtractor = MExtractor(mFilePath)
    override fun getFormat(): MediaFormat? = mExtractor.getVideoFormat()

    override fun readBuffer(buffer: ByteBuffer): Int = mExtractor.readBuffer(buffer)

    override fun getCurrentTimestamp(): Long = mExtractor.getCurrentTimeStamp()

    override fun getSampleFlags(): Int {
        return mExtractor.getSampleFlags()
    }

    override fun seek(pos: Long): Long = mExtractor.seek(pos)

    override fun setStartPos(pos: Long) {
        mExtractor.setStartPos(pos)
    }

    override fun stop() {
        mExtractor.stop()
    }
}