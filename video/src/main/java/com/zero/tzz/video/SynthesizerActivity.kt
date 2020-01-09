package com.zero.tzz.video

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.util.Size
import android.view.Surface
import androidx.appcompat.app.AppCompatActivity
import com.zero.tzz.video.media.Frame
import com.zero.tzz.video.media.decoder.*
import com.zero.tzz.video.media.encoder.AudioEncoder
import com.zero.tzz.video.media.encoder.BaseEncoder
import com.zero.tzz.video.media.encoder.IEncodeStateListener
import com.zero.tzz.video.media.encoder.VideoEncoder
import com.zero.tzz.video.media.muxer.MMuxer
import com.zero.tzz.video.opengl.drawer.VideoDrawer
import com.zero.tzz.video.opengl.egl.CustomGLRender
import kotlinx.android.synthetic.main.activity_encode_egl_player.*
import java.util.concurrent.Executors

/**
 *
 * @author Zero_Tzz
 * @date 2020-01-08 15:39
 * @description SynthesizerActivity
 */
class SynthesizerActivity : AppCompatActivity(), MMuxer.IMuxerStateListener {
    val path = "/sdcard/big_buck_bunny.mp4"
    private val threadPool = Executors.newFixedThreadPool(10)

    private var mRender = CustomGLRender()

    private var audioDecoder: IDecoder? = null
    private var videoDecoder: IDecoder? = null

    private lateinit var videoEncoder: VideoEncoder
    private lateinit var audioEncoder: AudioEncoder

    private var mMuxer = MMuxer(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_egl_player)
        btn_start.setOnClickListener {
            start()
        }
    }

    private fun start() {
        btn_start.text = "正在解码"
        btn_start.isEnabled = false
        initVideo()
        initAudio()
        initVideoEncoder()
        initAudioEncoder()

    }

    private fun initVideo() {
        VideoDrawer().apply {
            getSurfaceTexture {
                initPlayer(path, Surface(it))
            }
            val size = getVideoSize(path)
            setVideoSize(size.width, size.height)
            mRender.addDrawer(this)
        }
    }

    private fun initAudio() {
        audioDecoder?.stop()
        audioDecoder = AudioDecoder(path).withoutSync()
        audioDecoder!!.setDecodeListener(object : DefDecodeStateListener {

            override fun decodeOneFrame(decodeJob: BaseDecoder?, frame: Frame) {
                audioEncoder.encodeOneFrame(frame)
            }

            override fun decodeFinished(decodeJob: BaseDecoder?) {
                audioEncoder.endOfStream()
            }
        })
        audioDecoder!!.resume()
        threadPool.execute(audioDecoder)
    }

    private fun initVideoEncoder() {
        val size = getVideoSize(path)
        videoEncoder = VideoEncoder(mMuxer, size.width, size.height)
        mRender.setRenderMode(CustomGLRender.RenderMode.RENDER_WHEN_DIRTY)
        mRender.setSurface(videoEncoder.getEncodeSurface()!!, size.width, size.height)
        videoEncoder.setStateListener(object : IEncodeStateListener {
            override fun encodeFinish(encoder: BaseEncoder) {
                mRender.stop()
            }
        })
        threadPool.execute(videoEncoder)
    }

    private fun initAudioEncoder() {
        // 音频编码器
        audioEncoder = AudioEncoder(mMuxer)
        threadPool.execute(audioEncoder)
    }

    private fun getVideoSize(path: String): Size {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(this, Uri.parse(path))
        val width =
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
        val height =
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
        mediaMetadataRetriever.release()
        return Size(width.toInt(), height.toInt())
    }

    private fun initPlayer(path: String, surface: Surface) {
        videoDecoder = VideoDecoder(path, surface = surface)
        audioDecoder = AudioDecoder(path)
        threadPool.execute(videoDecoder)
        threadPool.execute(audioDecoder)
        audioDecoder?.resume()
        videoDecoder?.resume()
    }

    override fun onMuxerFinish() {
        runOnUiThread {
            btn_start.isEnabled = true
            btn_start.text = "编码完成"
        }
        audioDecoder?.stop()
        videoDecoder?.stop()
        audioDecoder = null
        videoDecoder = null
    }
}