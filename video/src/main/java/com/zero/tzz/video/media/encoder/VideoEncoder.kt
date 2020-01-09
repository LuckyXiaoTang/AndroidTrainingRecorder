package com.zero.tzz.video.media.encoder

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.os.Build
import android.util.Log
import android.view.Surface
import com.zero.tzz.video.media.muxer.MMuxer
import java.nio.ByteBuffer

/**
 *
 * @author Zero_Tzz
 * @date 2020-01-08 14:28
 * @description VideoEncoder
 */
class VideoEncoder(
    muxer: MMuxer,
    width: Int = -1,
    height: Int = -1
) : BaseEncoder(muxer, width, height) {
    companion object {
        private const val TAG = "VideoEncoder"
        private const val DEFAULT_ENCODE_FRAME_RATE = 30
    }

    private var mSurface: Surface? = null

    override fun encodeType(): String = "video/avc"

    override fun configEncoder(codec: MediaCodec) {
        if (mWidth <= 0 || mHeight <= 0) {
            throw IllegalArgumentException("Encode width or height is invalid, width: $mWidth, height: $mHeight")
        }
        // bitrate = Width * Height * FrameRate * Factor
        // Factor: 0.1~0.2
        val bitrate = mWidth * mHeight * 3
        val format = MediaFormat.createVideoFormat(encodeType(), mWidth, mHeight)
        format.setInteger(MediaFormat.KEY_BIT_RATE, bitrate)
        format.setInteger(MediaFormat.KEY_FRAME_RATE, DEFAULT_ENCODE_FRAME_RATE)
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
        format.setInteger(
            MediaFormat.KEY_COLOR_FORMAT,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
        )
        // - BITRATE_MODE_CQ 忽略用户设置的码率，由编码器自己控制码率，并尽可能保证画面清晰度和码率的均衡
        // - BITRATE_MODE_CBR 无论视频的画面内容如果，尽可能遵守用户设置的码率
        // - BITRATE_MODE_VBR 尽可能遵守用户设置的码率，但是会根据帧画面之间运动矢量
        // （通俗理解就是帧与帧之间的画面变化程度）来动态调整码率，如果运动矢量较大，则在该时间段将码率调高，如果画面变换很小，则码率降低。
        try {
            configEncoderWithCQ(codec, format)
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                configEncoderWithVBR(codec, format)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "配置视频编码器失败")
            }
        }
        mSurface = codec.createInputSurface()
    }

    override fun addTrack(muxer: MMuxer, format: MediaFormat) {
        muxer.addVideoTrack(format)
    }

    override fun writeData(muxer: MMuxer, buffer: ByteBuffer, info: MediaCodec.BufferInfo) {
        muxer.writeVideoData(buffer, info)
    }

    override fun release(muxer: MMuxer) {
        muxer.release()
    }

    private fun configEncoderWithCQ(
        codec: MediaCodec,
        format: MediaFormat
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 部分手机不支持 BITRATE_MODE_CQ 模式，有可能会异常
            format.setInteger(
                MediaFormat.KEY_BITRATE_MODE,
                MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CQ
            )
        }
        codec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
    }

    private fun configEncoderWithVBR(
        codec: MediaCodec,
        format: MediaFormat
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            format.setInteger(
                MediaFormat.KEY_BITRATE_MODE,
                MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_VBR
            )
        }
        codec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
    }

    fun getEncodeSurface(): Surface? = mSurface
}