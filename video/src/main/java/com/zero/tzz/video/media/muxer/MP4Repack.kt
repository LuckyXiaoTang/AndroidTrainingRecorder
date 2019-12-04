package com.zero.tzz.video.media.muxer

import android.media.MediaCodec
import android.util.Log
import com.zero.tzz.video.media.extractor.AudioExtractor
import com.zero.tzz.video.media.extractor.VideoExtractor
import java.nio.ByteBuffer

/**
 *
 * @author Zero_Tzz
 * @date 2019-11-28 14:45
 * @description MP4Repack
 */
class MP4Repack(filePath: String) {
    private val TAG = "MP4Repack"

    private val mAudioExtractor = AudioExtractor(filePath)
    private val mVideoExtractor = VideoExtractor(filePath)

    private val mMuxer = MMuxer()

    fun start() {
        val audioFormat = mAudioExtractor.getFormat()
        val videoFormat = mVideoExtractor.getFormat()

        // 添加音视频轨道
        mMuxer.addVideoTrack(videoFormat)
        mMuxer.addAudioTrack(audioFormat)

        // 视频混合
        Thread {
            val buffer = ByteBuffer.allocate(500 * 1024)
            val bufferInfo = MediaCodec.BufferInfo()

            // 视频分离、写入
            if (videoFormat != null) {
                var size = mVideoExtractor.readBuffer(buffer)
                while (size > 0) {
                    bufferInfo.set(
                        0,
                        size,
                        mVideoExtractor.getCurrentTimestamp(),
                        mVideoExtractor.getSampleFlags()
                    )
                    mMuxer.writeVideoData(buffer, bufferInfo)
                    size = mVideoExtractor.readBuffer(buffer)
                }
            }

            // 音频分离、写入
            if (audioFormat != null) {
                var size = mAudioExtractor.readBuffer(buffer)
                while (size > 0) {
                    bufferInfo.set(
                        0,
                        size,
                        mAudioExtractor.getCurrentTimestamp(),
                        mAudioExtractor.getSampleFlags()
                    )
                    mMuxer.writeAudioData(buffer, bufferInfo)
                    size = mAudioExtractor.readBuffer(buffer)
                }
            }

            mAudioExtractor.stop()
            mVideoExtractor.stop()
            mMuxer.release()
            Log.d(TAG, "打包完成")
        }.start()
    }
}