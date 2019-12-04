package com.zero.tzz.video

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zero.tzz.video.media.decoder.AudioDecoder
import com.zero.tzz.video.media.decoder.VideoDecoder
import com.zero.tzz.video.media.muxer.MP4Repack
import kotlinx.android.synthetic.main.activity_simple_player.*
import java.util.concurrent.Executors

/**
 *
 * @author Zero_Tzz
 * @date 2019-11-29 13:03
 * @description SimplePlayerActivity
 */

class SimplePlayerActivity : AppCompatActivity() {
    val path = "/sdcard/big_buck_bunny.mp4"
    private var videoDecoder: VideoDecoder? = null
    private var audioDecoder: AudioDecoder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_player)
        initPlayer()
        btn_pack.setOnClickListener {
            MP4Repack(path).start()
        }
    }

    private fun initPlayer() {
        // 创建线程池
        val threadPool = Executors.newFixedThreadPool(10)

        // 创建视频解码器
        videoDecoder = VideoDecoder(path, sfv)
        threadPool.execute(videoDecoder)

        // 创建音频解码器
        audioDecoder = AudioDecoder(path)
        threadPool.execute(audioDecoder)

        videoDecoder?.resume()
        audioDecoder?.resume()
    }

    override fun onDestroy() {
        videoDecoder?.stop()
        audioDecoder?.stop()
        super.onDestroy()
    }
}