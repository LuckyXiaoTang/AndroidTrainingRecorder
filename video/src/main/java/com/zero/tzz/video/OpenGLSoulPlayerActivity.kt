package com.zero.tzz.video

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.util.Size
import android.view.Surface
import androidx.appcompat.app.AppCompatActivity
import com.zero.tzz.video.media.IDecoder
import com.zero.tzz.video.media.decoder.AudioDecoder
import com.zero.tzz.video.media.decoder.VideoDecoder
import com.zero.tzz.video.opengl.SimpleRender
import com.zero.tzz.video.opengl.drawer.IDrawer
import com.zero.tzz.video.opengl.drawer.SoulVideoDrawer
import kotlinx.android.synthetic.main.activity_simple_render.*
import java.util.concurrent.Executors

/**
 *
 * @author Zero_Tzz
 * @date 2019-11-29 13:08
 * @description SimpleRenderActivity
 */
class OpenGLSoulPlayerActivity : AppCompatActivity() {
    val path = "/sdcard/big_buck_bunny.mp4"
    private lateinit var mDrawer: IDrawer
    private var mVideoDecoder: IDecoder? = null
    private var mAudioDecoder: IDecoder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opengl_player)

        initRender()
    }

    private fun initRender() {
        mDrawer = SoulVideoDrawer().apply {
            getSurfaceTexture {
                initPlayer(Surface(it))
            }
        }
        val size = getVideoSize()
        mDrawer.setVideoSize(size.width, size.height)
        gl_sfv.setEGLContextClientVersion(2)
        gl_sfv.setRenderer(SimpleRender().apply {
            addDrawer(mDrawer)
        })
    }

    private fun getVideoSize(): Size {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(this, Uri.parse(path))
        val width =
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
        val height =
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
        return Size(width.toInt(), height.toInt())
    }

    private fun initPlayer(surface: Surface) {
        val threadPool = Executors.newFixedThreadPool(10)

        mVideoDecoder = VideoDecoder(path, surface = surface)
        mAudioDecoder = AudioDecoder(path)

        threadPool.execute(mVideoDecoder)
        threadPool.execute(mAudioDecoder)

        mVideoDecoder?.resume()
        mAudioDecoder?.resume()
    }

    override fun onDestroy() {
        mDrawer.release()
        mVideoDecoder?.stop()
        mAudioDecoder?.stop()
        super.onDestroy()
    }
}