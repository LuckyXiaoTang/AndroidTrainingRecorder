package com.zero.tzz.video.media.muxer

import android.media.MediaCodec
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Environment
import android.util.Log
import java.io.File
import java.nio.ByteBuffer

/**
 *
 * @author Zero_Tzz
 * @date 2019-11-28 11:49
 * @description MMuxer
 */
class MMuxer {
    private val TAG = "MMuxer"
    /** 输出文件路径 */
    private var mFilePath: String? = null

    /** 混合器 */
    private var mMuxer: MediaMuxer? = null

    private var mVideoTrackIndex = -1
    private var mAudioTrackIndex = -1

    private var mVideoTrackAdd = false
    private var mAudioTrackAdd = false

    private var mIsStart = false

    init {
        val folder = Environment.getExternalStorageDirectory().absolutePath
        val fileName = "Muxer - ${System.currentTimeMillis()}.mp4"
        mFilePath = folder + File.separator + fileName
        mMuxer = MediaMuxer(mFilePath!!, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
    }

    /** 添加视频轨道 */
    fun addVideoTrack(format: MediaFormat?) {
        mMuxer?.let {
            if (format == null) {
                ignoreVideo()
            } else {
                mVideoTrackIndex = it.addTrack(format)
                mVideoTrackAdd = true
                startMuxer()
            }
        }
    }

    /** 添加音频轨道 */
    fun addAudioTrack(format: MediaFormat?) {
        mMuxer?.let {
            if (format == null) {
                ignoreAudio()
            } else {
                mAudioTrackIndex = it.addTrack(format)
                mAudioTrackAdd = true
                startMuxer()
            }
        }
    }

    /** 忽略音频轨道 */
    private fun ignoreAudio() {
        if (mAudioTrackAdd) return
        mAudioTrackAdd = true
        startMuxer()
    }

    /** 忽略视频轨道 */
    private fun ignoreVideo() {
        if (mVideoTrackAdd) return
        mVideoTrackAdd = true
        startMuxer()
    }

    fun writeVideoData(buffer: ByteBuffer, info: MediaCodec.BufferInfo) {
        if (mIsStart) {
            mMuxer?.writeSampleData(mVideoTrackIndex, buffer, info)
        }
    }

    fun writeAudioData(buffer: ByteBuffer, info: MediaCodec.BufferInfo) {
        if (mIsStart) {
            mMuxer?.writeSampleData(mAudioTrackIndex, buffer, info)
        }
    }

    fun startMuxer() {
        if (mVideoTrackAdd && mAudioTrackAdd) {
            mMuxer?.start()
            mIsStart = true
            Log.d(TAG, "开启混合器")
        }
    }

    fun getFilePath(): String? = mFilePath

    fun release() {
        mAudioTrackAdd = false
        mVideoTrackAdd = false
        try {
            mMuxer?.stop()
            mMuxer?.release()
            mMuxer = null
            Log.d(TAG, "停止混合器")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}