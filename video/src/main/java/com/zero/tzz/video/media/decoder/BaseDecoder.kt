package com.zero.tzz.video.media.decoder

import android.media.MediaCodec
import android.media.MediaFormat
import android.os.SystemClock
import android.util.Log
import com.zero.tzz.video.media.Frame
import com.zero.tzz.video.media.extractor.IExtractor
import java.io.File
import java.nio.ByteBuffer

/**
 *
 * @author Zero_Tzz
 * @date 2019-11-26 11:43
 * @description BaseDecoder
 */
abstract class BaseDecoder(val mFilePath: String) : IDecoder {
    private val TAG = "BaseDecoder"
    //////////////////////////////////////////// 线程相关 ////////////////////////////////////////////
    /** 解码器是否在运行 */
    private var mIsRunning = true

    /** 线程等待锁 */
    private var mSync = Object()

    /** 是否已准备好解码 */
    private var mReadyDecoder = false

    //////////////////////////////////////////// 解码相关 ////////////////////////////////////////////

    /** 音视频解码器 */
    protected var mCodec: MediaCodec? = null

    /** 音视频读取器 */
    protected var mExtractor: IExtractor? = null

    /** 解码输入流缓冲区 */
    protected var mInputBuffers: Array<ByteBuffer>? = null

    /** 解码输出流缓冲区 */
    protected var mOutputBuffers: Array<ByteBuffer>? = null

    /** 解码数据信息 */
    private var mBufferInfo = MediaCodec.BufferInfo()

    /** 解码状态 */
    private var mState = DecodeState.STOP

    /** 监听 */
    protected var mStateDecodeListener: IDecodeStateListener? = null

    /** 视频宽 */
    protected var mWidth = 0

    /** 视频高 */
    protected var mHeight = 0

    private var mDuration = 0L

    private var mStartPos = 0L

    private var mEndPos = 0L

    /** 开始解码时间，用于音视频同步 */
    private var mStartTimeForSync = -1L

    /** 流数据是否结束 */
    private var mIsEOS = false

    /** 是否需要音视频渲染同步 */
    private var mSyncRender = true


    override fun run() {
        if (mState == DecodeState.STOP) {
            mState = DecodeState.START
        }
        mStateDecodeListener?.decodePrepare(this)

        // 1.初始化,启动解码器
        if (!init()) return
        Log.d(TAG, "开始解码")
        while (mIsRunning) {
            if (mState != DecodeState.START &&
                mState != DecodeState.DECODING &&
                mState != DecodeState.SEEKING
            ) {
                Log.d(TAG, "等待：$mState")
                waitDecode()
                // 同步时间矫正 - 恢复同步的起始时间，即去除等待流失的时间
                mStartTimeForSync = System.currentTimeMillis() - getCurTimestamp()
            }

            if (!mIsRunning || mState == DecodeState.STOP) {
                mIsRunning = false
                break
            }

            // 初始化同步时间
            if (mStartTimeForSync == -1L) {
                mStartTimeForSync = System.currentTimeMillis()
            }

            // 2.推送数据到解码器
            if (!mIsEOS) {
                mIsEOS = pushBufferToDecoder()
            }

            // 3.从解码器拉取解码后的数据
            val index = pullBufferFromDecoder()
            if (index >= 0 && mOutputBuffers != null) {
                // 音视频同步
                if (mSyncRender && mState == DecodeState.DECODING) {
                    sleepRender()
                }
                // 4.渲染
                if (mSyncRender) {
                    render(mOutputBuffers!![index], mBufferInfo)
                }

                val frame = Frame()
                frame.buffer = mOutputBuffers!![index]
                frame.setBufferInfo(mBufferInfo)
                mStateDecodeListener?.decodeOneFrame(this, frame)

                // 5.释放
                mCodec?.releaseOutputBuffer(index, true)
                if (mState == DecodeState.START) {
                    mState = DecodeState.PAUSE
                }
            }

            // 6.判断解码释放完成
            if (mBufferInfo.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                mState = DecodeState.FINISH
                mStateDecodeListener?.decodeFinished(this)
            }
        }
        decodeFinished()
        // 7.释放解码器
        release()
    }

    private fun init(): Boolean {
        // 1.检查参数完整性
        if (mFilePath.isEmpty() || !File(mFilePath).exists()) {
            Log.e(TAG, "文件不存在:$mFilePath")
            mStateDecodeListener?.decodeError(this, "文件不存在$mFilePath")
            return false
        }
        // 子类检查参数完整性
        if (!check()) return false

        // 2.初始化音视频提取器
        mExtractor = initExtractor(mFilePath)
        if (mExtractor == null || mExtractor!!.getFormat() == null) return false

        // 3.初始化参数
        if (!initParams()) return false

        // 4.初始化渲染器
        if (!initRender()) return false

        // 5.初始化解码器
        if (!initCodec()) return false
        return true
    }

    private fun initParams(): Boolean {
        try {
            val format = mExtractor!!.getFormat()!!
            mDuration = format.getLong(MediaFormat.KEY_DURATION) / 1000
            initSpecParams(format)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    private fun initCodec(): Boolean {
        try {
            // 1.根据音视频b编码格式初始化编码器
            val format = mExtractor!!.getFormat()!!
            val mine = mExtractor!!.getFormat()!!.getString(MediaFormat.KEY_MIME)
            mCodec = MediaCodec.createDecoderByType(mine)

            // 2.配置解码器
            if (!configCodec(mCodec!!, format)) {
                waitDecode()
            }

            // 3.启动编码器
            mCodec!!.start()

            // 4.获取缓冲区
            mInputBuffers = mCodec!!.inputBuffers
            mOutputBuffers = mCodec!!.outputBuffers
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    /** 等待解码器 */
    private fun waitDecode() {
        try {
            if (mState == DecodeState.PAUSE) {
                mStateDecodeListener?.decodePause(this)
            }
            synchronized(mSync) { mSync.wait() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** 解码器继续执行 */
    protected fun notifyDecode() {
        synchronized(mSync) { mSync.notifyAll() }
        if (mState == DecodeState.DECODING) {
            mStateDecodeListener?.decodeDecoding(this)
        }
    }

    private fun pushBufferToDecoder(): Boolean {
        val inputBufferIndex = mCodec!!.dequeueInputBuffer(1000)
        var isEndOfStream = false
        if (inputBufferIndex >= 0) {
            val inputBuffer = mInputBuffers!![inputBufferIndex]
            val sampleSize = mExtractor!!.readBuffer(inputBuffer)
            if (sampleSize < 0) {
                // 如果数据取完，压入结束Flag
                mCodec!!.queueInputBuffer(
                    inputBufferIndex,
                    0,
                    0,
                    0,
                    MediaCodec.BUFFER_FLAG_END_OF_STREAM
                )
                isEndOfStream = true
            } else {
                mCodec!!.queueInputBuffer(
                    inputBufferIndex,
                    0,
                    sampleSize,
                    mExtractor!!.getCurrentTimestamp(),
                    0
                )
            }
        }
        return isEndOfStream
    }

    private fun pullBufferFromDecoder(): Int {
        // 查询是否有解码完成的数据，index >=0 时，表示数据有效，并且index为缓冲区索引
        val outputBufferIndex = mCodec!!.dequeueOutputBuffer(mBufferInfo, 1000)
        when (outputBufferIndex) {
            MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
            }
            MediaCodec.INFO_TRY_AGAIN_LATER -> {
            }
            MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED -> {
                mOutputBuffers = mCodec!!.outputBuffers
            }
            else -> {
                return outputBufferIndex
            }
        }
        return -1
    }

    private fun sleepRender() {
        val passTime = System.currentTimeMillis() - mStartTimeForSync
        val curTime = getCurTimestamp()
        if (curTime > passTime) {
            SystemClock.sleep(curTime - passTime)
        }
    }

    override fun getCurTimestamp(): Long {
        return mBufferInfo.presentationTimeUs / 1000
    }

    private fun release() {
        try {
            mState = DecodeState.STOP
            mIsEOS = false
            mExtractor?.stop()
            mCodec?.stop()
            mCodec?.release()
            mStateDecodeListener?.decoderDestroy(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun pause() {
        mState = DecodeState.PAUSE
    }

    override fun resume() {
        mState = DecodeState.DECODING
        notifyDecode()
    }

    override fun stop() {
        mState = DecodeState.STOP
        mIsRunning = false
    }

    override fun isSeeking(): Boolean = mState == DecodeState.SEEKING

    override fun isDecoding(): Boolean = mState == DecodeState.DECODING

    override fun isStop(): Boolean = mState == DecodeState.STOP

    override fun getWidth(): Int = mWidth

    override fun getHeight(): Int = mHeight

    override fun getDuration(): Long = mDuration

    override fun getRotateAngle(): Int = 0

    override fun getMediaFormat(): MediaFormat? = mExtractor?.getFormat()

    override fun getTrack(): Int = 0

    override fun getFilePath(): String = mFilePath

    override fun setDecodeListener(listener: IDecodeStateListener?) {
        mStateDecodeListener = listener
    }

    override fun withoutSync(): IDecoder {
        mSyncRender = false
        return this
    }

    /** 主要检查子类的一些空值情况 */
    abstract fun check(): Boolean

    /** 子类初始化音视频读取器 */
    abstract fun initExtractor(filePath: String): IExtractor

    /** 子类初始化一些Format相关的参数 */
    abstract fun initSpecParams(format: MediaFormat)

    /** 配置编码器 */
    abstract fun configCodec(codec: MediaCodec, format: MediaFormat): Boolean

    /** 子类初始化渲染器 */
    abstract fun initRender(): Boolean

    /** 渲染，主要交给子类操作 */
    abstract fun render(byteBuffer: ByteBuffer, info: MediaCodec.BufferInfo)

    /** 解码结束 */
    abstract fun decodeFinished()
}