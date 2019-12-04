package com.zero.tzz.video.media.extractor

import android.media.MediaExtractor
import android.media.MediaFormat
import java.nio.ByteBuffer

/**
 *
 * @author Zero_Tzz
 * @date 2019-11-26 18:12
 * @description MExtractor
 */
class MExtractor(mFilePath: String) {

    companion object {
        const val PREFIX_VIDEO = "video/"
        const val PREFIX_AUDIO = "audio/"
    }

    /** 音视频分离器 */
    private var mExtractor: MediaExtractor? = null

    /** 音频通道索引 */
    private var mAudioTrack: Int = -1

    /** 视频通道索引 */
    private var mVideoTrack: Int = -1

    /** 当前帧时间戳 */
    private var mCurrentSampleTime = 0L

    /** 当前帧类型 */
    private var mCurrentSampleFlags = 0

    /** 开始时间 */
    private var mStartPos = 0L

    init {
        mExtractor = MediaExtractor()
        mExtractor?.setDataSource(mFilePath)
    }

    /** 获取视频格式参数 */
    fun getVideoFormat(): MediaFormat? {
        for (i in 0 until mExtractor!!.trackCount) {
            val mediaFormat = mExtractor!!.getTrackFormat(i)
            val mine = mediaFormat.getString(MediaFormat.KEY_MIME)
            if (mine.startsWith(PREFIX_VIDEO)) {
                mVideoTrack = i
                break
            }
        }
        return if (mVideoTrack >= 0) {
            mExtractor!!.getTrackFormat(mVideoTrack)
        } else {
            null
        }
    }

    /** 获取音频格式参数 */
    fun getAudioFormat(): MediaFormat? {
        for (i in 0 until mExtractor!!.trackCount) {
            val mediaFormat = mExtractor!!.getTrackFormat(i)
            val mine = mediaFormat.getString(MediaFormat.KEY_MIME)
            if (mine.startsWith(PREFIX_AUDIO)) {
                mAudioTrack = i
                break
            }
        }
        return if (mAudioTrack >= 0) {
            mExtractor!!.getTrackFormat(mAudioTrack)
        } else {
            null
        }
    }

    /** 读取音视频数据 */
    fun readBuffer(byteBuffer: ByteBuffer): Int {
        byteBuffer.clear()
        selectSourceTrack()
        val sampleData = mExtractor!!.readSampleData(byteBuffer, 0)
        if (sampleData < 0) {
            return -1
        }
        mCurrentSampleTime = mExtractor!!.sampleTime
        mCurrentSampleFlags = mExtractor!!.sampleFlags
        mExtractor!!.advance()
        return sampleData
    }

    /** 停止读取音视频数据 */
    fun stop() {
        mExtractor?.release()
        mExtractor = null
    }

    /** 选择通道 */
    private fun selectSourceTrack() {
        when {
            mVideoTrack >= 0 -> {
                mExtractor!!.selectTrack(mVideoTrack)
            }
            mAudioTrack >= 0 -> {
                mExtractor!!.selectTrack(mAudioTrack)
            }
        }
    }

    /** seek到指定位置，返回实际帧位置 */
    fun seek(pos: Long): Long {
        mExtractor!!.seekTo(pos, MediaExtractor.SEEK_TO_PREVIOUS_SYNC)
        return mExtractor!!.sampleTime
    }

    /** 设置开始时间 */
    fun setStartPos(pos: Long) {
        mStartPos = pos
    }

    /** 获取视频通道 */
    fun getVideoTracker(): Int = mVideoTrack

    /** 获取音频通道 */
    fun getAudioTracker(): Int = mAudioTrack

    /** 获取当前帧时间 */
    fun getCurrentTimeStamp(): Long = mCurrentSampleTime


    fun getSampleFlags(): Int = mCurrentSampleFlags
}