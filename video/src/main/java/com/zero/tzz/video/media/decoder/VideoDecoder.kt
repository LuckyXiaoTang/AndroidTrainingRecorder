package com.zero.tzz.video.media.decoder

import android.media.MediaCodec
import android.media.MediaFormat
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.zero.tzz.video.media.extractor.IExtractor
import com.zero.tzz.video.media.extractor.VideoExtractor
import java.nio.ByteBuffer

/**
 *
 * @author Zero_Tzz
 * @date 2019-11-26 17:56
 * @description VideoDecoder
 */
class VideoDecoder(
    filePath: String,
    private var surfaceView: SurfaceView? = null,
    private var surface: Surface? = null
) : BaseDecoder(filePath) {

    private val TAG = "VideoDecoder"


    override fun check(): Boolean {
        if (surface == null && surfaceView == null) {
            Log.e(TAG, "SurfaceView和Surface都为空，至少需要一个不为空")
            mStateDecodeListener?.decodeError(this, "SurfaceView和Surface都为空，至少需要一个不为空")
            return false
        }
        return true
    }

    override fun initExtractor(filePath: String): IExtractor = VideoExtractor(filePath)

    override fun initSpecParams(format: MediaFormat) {

    }

    override fun configCodec(codec: MediaCodec, format: MediaFormat): Boolean {
        if (surface != null) {
            codec.configure(format, surface, null, 0)
            notifyDecode()
        } else {
            surfaceView?.holder?.addCallback(object : SurfaceHolder.Callback2 {
                override fun surfaceRedrawNeeded(holder: SurfaceHolder?) {

                }

                override fun surfaceChanged(
                    holder: SurfaceHolder?,
                    format: Int,
                    width: Int,
                    height: Int
                ) {

                }

                override fun surfaceDestroyed(holder: SurfaceHolder?) {

                }

                override fun surfaceCreated(holder: SurfaceHolder?) {
                    surface = holder?.surface
                    configCodec(codec, format)
                }
            })
            return false
        }
        return true
    }

    override fun initRender(): Boolean {
        return true
    }

    override fun render(byteBuffer: ByteBuffer, info: MediaCodec.BufferInfo) {

    }

    override fun decodeFinished() {

    }

}