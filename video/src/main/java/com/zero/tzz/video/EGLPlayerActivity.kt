package com.zero.tzz.video

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Size
import android.view.Surface
import androidx.appcompat.app.AppCompatActivity
import com.zero.tzz.video.media.decoder.AudioDecoder
import com.zero.tzz.video.media.decoder.VideoDecoder
import com.zero.tzz.video.opengl.drawer.VideoDrawer
import com.zero.tzz.video.opengl.egl.CustomGLRender
import kotlinx.android.synthetic.main.activity_egl_player.*
import java.util.concurrent.Executors

/**
 *
 * @author Zero_Tzz
 * @date 2019-12-03 17:04
 * @description EGLPlayerActivity
 */
class EGLPlayerActivity : AppCompatActivity() {
    val path = "/sdcard/big_buck_bunny.mp4"
    val path2 = "/sdcard/big_buck_bunny_2.mp4"
    private val mRender = CustomGLRender()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_egl_player)
        initFirstPlayer()
        initSecondPlayer()
        initRender()
    }

    private fun initFirstPlayer() {
        VideoDrawer().apply {
            getSurfaceTexture {
                initPlayer(path, Surface(it), true)
            }
            val size = getVideoSize(path)
            setVideoSize(size.width, size.height)
            mRender.addDrawer(this)
        }
    }

    private fun initSecondPlayer() {
        VideoDrawer().apply {
            getSurfaceTexture {
                initPlayer(path2, Surface(it), true)
            }
            val size = getVideoSize(path2)
            alpha(0.5f)
            setVideoSize(size.width, size.height)
            mRender.addDrawer(this)
            Handler().postDelayed({ scale(0.5f, 0.5f) }, 1000)
        }
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

    private fun initPlayer(path: String, surface: Surface, audio: Boolean) {
        val threadPool = Executors.newFixedThreadPool(2)
        val videoDecoder = VideoDecoder(path, surface = surface)
        val audioDecoder = AudioDecoder(path)

        threadPool.execute(videoDecoder)
        if (audio) {
            threadPool.execute(audioDecoder)
            audioDecoder.resume()
        }
        videoDecoder.resume()
    }

    private fun initRender() {
        mRender.setSurfaceView(sfv)
    }
}