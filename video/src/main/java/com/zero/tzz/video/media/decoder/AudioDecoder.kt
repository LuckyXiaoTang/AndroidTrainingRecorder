package com.zero.tzz.video.media.decoder

import android.media.*
import android.os.Build
import com.zero.tzz.video.media.extractor.AudioExtractor
import com.zero.tzz.video.media.extractor.IExtractor
import java.nio.ByteBuffer

/**
 *
 * @author Zero_Tzz
 * @date 2019-11-27 10:50
 * @description AudioDecoder
 */
class AudioDecoder(filePath: String) : BaseDecoder(filePath) {

    /** 采样率 */
    private var mSampleRate = -1

    /** 通道数 */
    private var mChannels = 1

    /** PCM采样位数 */
    private var mPCMEncodeBit = AudioFormat.ENCODING_PCM_16BIT

    /** 音频播放器 */
    private var mAudioTracker: AudioTrack? = null

    /** 音频数据缓存 */
    private var mAudioOutTempBuf: ShortArray? = null


    override fun check(): Boolean {
        return true
    }

    override fun initExtractor(filePath: String): IExtractor {
        return AudioExtractor(filePath)
    }

    override fun initSpecParams(format: MediaFormat) {
        try {
            mChannels = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
            mSampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
            mPCMEncodeBit = if (format.containsKey(MediaFormat.KEY_PCM_ENCODING)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    format.getInteger(MediaFormat.KEY_PCM_ENCODING)
                } else {
                    AudioFormat.ENCODING_PCM_16BIT
                }
            } else {
                AudioFormat.ENCODING_PCM_16BIT
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun configCodec(codec: MediaCodec, format: MediaFormat): Boolean {
        codec.configure(format, null, null, 0)
        return true
    }

    override fun initRender(): Boolean {
        val channel = if (mChannels == 1) {
            // 单声道
            AudioFormat.CHANNEL_OUT_MONO
        } else {
            // 双声道
            AudioFormat.CHANNEL_OUT_STEREO
        }

        val minBufferSize = AudioTrack.getMinBufferSize(mSampleRate, channel, mPCMEncodeBit)
        mAudioOutTempBuf = ShortArray(minBufferSize / 2)
        mAudioTracker = AudioTrack(
            AudioManager.STREAM_MUSIC,
            mSampleRate,
            channel,
            mPCMEncodeBit,
            minBufferSize,
            AudioTrack.MODE_STREAM)
        mAudioTracker!!.play()
        return true
    }

    override fun render(byteBuffer: ByteBuffer, info: MediaCodec.BufferInfo) {
        if (mAudioOutTempBuf!!.size < info.size / 2) {
            mAudioOutTempBuf = ShortArray(info.size / 2)
        }
        byteBuffer.position(0)
        byteBuffer.asShortBuffer().get(mAudioOutTempBuf, 0, info.size / 2)
        mAudioTracker!!.write(mAudioOutTempBuf!!, 0, info.size / 2)
    }

    override fun decodeFinished() {
        mAudioTracker?.stop()
        mAudioTracker?.release()
    }

}