package com.zero.tzz.video.media.encoder

import android.media.MediaCodec
import android.media.MediaFormat
import android.os.SystemClock
import com.zero.tzz.video.media.Frame
import com.zero.tzz.video.media.muxer.MMuxer
import java.nio.ByteBuffer

/**
 *
 * @author Zero_Tzz
 * @date 2020-01-08 13:30
 * @description BaseEncoder
 */
abstract class BaseEncoder(
    muxer: MMuxer,
    width: Int = -1,
    height: Int = -1
) : Runnable {
    private val TAG = "BaseEncoder"
    /** 目标视频宽，只有视频编码的时候才有效 */
    protected var mWidth = width
    /** 目标视频高，只有视频编码的时候才有效 */
    protected var mHeight = height
    /** Mp4合成器 */
    private var mMuxer = muxer
    /** 线程运行 */
    private var mRunning = true
    /** 编码帧序列 */
    private var mFrames = mutableListOf<Frame>()
    /** 编码器 */
    private lateinit var mCodec: MediaCodec
    /** 编码信息 */
    private val mBufferInfo = MediaCodec.BufferInfo()
    /** 编码输出缓冲区 */
    private lateinit var mOutputBuffers: Array<ByteBuffer>
    /** 编码输入缓冲区 */
    private lateinit var mInputBuffers: Array<ByteBuffer>
    /** 同步锁 */
    private val mSync = Object()
    /** 是否编码结束 */
    private var mIsEOS = false
    /** 编码状态监听 */
    private var mStateListener: IEncodeStateListener? = null

    /**
     * 是否手动编码
     * 视频：false 音频：true
     *
     * 注：视频编码通过Surface，MediaCodec自动完成编码；音频数据需要用户自己压入编码缓冲区，完成编码
     */
    open fun encodeManually() = true

    /**
     * 每一帧排队等待时间
     */
    open fun frameWaitTimeMs() = 20L

    init {
        initCodec()
    }

    private fun initCodec() {
        mCodec = MediaCodec.createEncoderByType(encodeType())
        configEncoder(mCodec)
        mCodec.start()
        mOutputBuffers = mCodec.outputBuffers
        mInputBuffers = mCodec.inputBuffers
    }

    override fun run() {
        loopEncode()
        done()
    }

    private fun loopEncode() {
        while (mRunning && !mIsEOS) {
            val empty = synchronized(mFrames) { mFrames.isEmpty() }
            if (empty) {
                waitEncode()
            }
            if (mFrames.isNotEmpty()) {
                val frame = synchronized(mFrames) { mFrames.removeAt(0) }
                when {
                    // 是否手动编码
                    encodeManually() -> {
                        // 1.压入数据 - 编码
                        encode(frame)
                    }
                    // 如果是自动编码（比如视频），遇到结束帧的时候，直接结束掉
                    frame.buffer == null -> {
                        mCodec.signalEndOfInputStream()
                        mIsEOS = true
                    }
                }
            }
            // 2.拉取编码好的数据
            drain()
        }
    }

    private fun encode(frame: Frame) {
        val index = mCodec.dequeueInputBuffer(-1)
        // 向编码器输入数据
        if (index >= 0) {
            val inputBuffer = mInputBuffers[index]
            inputBuffer.clear()
            if (frame.buffer != null) {
                inputBuffer.put(frame.buffer)
            }
            // 小于等于0时，为音频结束符标记
            if (frame.buffer == null || frame.bufferInfo.size <= 0) {
                mCodec.queueInputBuffer(
                    index,
                    0,
                    0,
                    frame.bufferInfo.presentationTimeUs,
                    MediaCodec.BUFFER_FLAG_END_OF_STREAM
                )
            } else {
                mCodec.queueInputBuffer(
                    index,
                    0,
                    frame.bufferInfo.size,
                    frame.bufferInfo.presentationTimeUs,
                    0
                )
            }
            frame.buffer?.clear()
        }
    }

    private fun drain() {
        loop@ while (!mIsEOS) {
            val index = mCodec.dequeueOutputBuffer(mBufferInfo, 0)
            when (index) {
                MediaCodec.INFO_TRY_AGAIN_LATER -> break@loop
                MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> addTrack(mMuxer, mCodec.outputFormat)
                MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED -> mOutputBuffers = mCodec.outputBuffers
                else -> {
                    if (index == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                        mIsEOS = true
                        mBufferInfo.set(0, 0, 0, mBufferInfo.flags)
                    }

                    if (mBufferInfo.flags == MediaCodec.BUFFER_FLAG_CODEC_CONFIG) {
                        // SPS or PPS, which should be passed by MediaFormat.
                        mCodec.releaseOutputBuffer(index, 0)
                        continue@loop
                    }
                    if (!mIsEOS) {
                        writeData(mMuxer, mOutputBuffers[index], mBufferInfo)

                    }
                    mCodec.releaseOutputBuffer(index, false)
                }
            }
        }
    }

    private fun done() {
        try {
            release(mMuxer)
            mCodec.stop()
            mCodec.release()
            mRunning = false
            mStateListener?.encodeFinish(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun waitEncode() {
        try {
            synchronized(mSync) { mSync.wait(1000) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun notifyEncode() {
        try {
            synchronized(mSync) { mSync.notifyAll() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    //////////////////////////////////////////// 外部调用 ////////////////////////////////////////////
    /**
     * 将一帧数据压入队列，等待编码
     */
    fun encodeOneFrame(frame: Frame) {
        synchronized(mFrames) {
            mFrames.add(frame)
            notifyEncode()
        }
        // 延时一点时间，避免掉帧
        SystemClock.sleep(frameWaitTimeMs())
    }

    /**
     * 通知结束编码
     */
    fun endOfStream() {
        synchronized(mFrames) {
            val frame = Frame()
            frame.buffer = null
            mFrames.add(frame)
            notifyEncode()
        }
    }

    fun setStateListener(listener: IEncodeStateListener?) {
        mStateListener = listener
    }

    /** 编码类型 */
    abstract fun encodeType(): String

    /** 子类配置编码器 */
    abstract fun configEncoder(codec: MediaCodec)

    abstract fun addTrack(muxer: MMuxer, format: MediaFormat)

    abstract fun writeData(muxer: MMuxer, buffer: ByteBuffer, info: MediaCodec.BufferInfo)

    abstract fun release(muxer: MMuxer)
}