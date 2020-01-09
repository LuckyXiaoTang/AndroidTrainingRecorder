package com.zero.tzz.video.opengl.egl

import android.opengl.GLES20
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import com.zero.tzz.video.opengl.GLTools
import com.zero.tzz.video.opengl.drawer.IDrawer
import java.lang.ref.WeakReference

/**
 *
 * @author Zero_Tzz
 * @date 2019-12-04 10:28
 * @description CustomGLRender
 */
class CustomGLRender : SurfaceHolder.Callback {

    /** OpenGL渲染线程 */
    private val mRenderThread = RenderThread()

    /** 显示用SurfaceView */
    private var mWeakSurfaceView: WeakReference<SurfaceView>? = null

    /** 绘制器列表 */
    private val mDrawers = mutableListOf<IDrawer>()

    private var mSurface: Surface? = null

    init {
        mRenderThread.start()
    }

    fun setSurfaceView(surfaceView: SurfaceView) {
        mWeakSurfaceView = WeakReference(surfaceView)
        surfaceView.holder.addCallback(this)

        surfaceView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewDetachedFromWindow(v: View?) {
                mRenderThread.onSurfaceStop()
            }

            override fun onViewAttachedToWindow(v: View?) {

            }
        })
    }

    fun setSurface(surface: Surface, width: Int, height: Int) {
        mSurface = surface
        mRenderThread.onSurfaceCreated()
        mRenderThread.onSurfaceChanged(width, height)
    }

    /** 设置渲染模式 RenderMode见下面 */
    fun setRenderMode(renderMode: RenderMode) {
        mRenderThread.setRenderMode(renderMode)
    }

    fun notitySwap(timeUs: Long) {
        mRenderThread.notifySwap(timeUs)
    }

    fun addDrawer(drawer: IDrawer) {
        mDrawers.add(drawer)
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        mRenderThread.onSurfaceCreated()
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        mRenderThread.onSurfaceChanged(width, height)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        mRenderThread.onSurfaceDestroyed()
    }

    fun stop() {
        mRenderThread.onSurfaceStop()
        mSurface = null
    }

    inner class RenderThread : Thread() {
        private var mRenderMode: RenderMode = RenderMode.RENDER_WHEN_DIRTY
        /** 渲染状态 */
        private var mState = RenderState.NO_SURFACE

        private var mEGLSurface: EGLSurfaceHolder? = null

        /** 是否已绑定Surface */
        private var mBindedEGLSurface = false

        /** 是否已经创建过EGL上下文，用于判断是否需要重新生成纹理ID */
        private var mCreatedEGLContext = false

        private var mCurTimestamp = 0L
        private var mLastTimestamp = 0L

        private var mWidth = 0
        private var mHeight = 0

        /** 同步锁 */
        private val mSync = Object()

        //////////////////////////////////////////// 线程相关 ////////////////////////////////////////////
        private fun waitRender() {
            synchronized(mSync) { mSync.wait() }
        }

        private fun notifyRender() {
            synchronized(mSync) { mSync.notifyAll() }
        }

        //////////////////////////////////////////// Surface生命周期相关 ////////////////////////////////////////////

        fun onSurfaceCreated() {
            mState = RenderState.FRESH_SURFACE
            notifyRender()
        }

        fun onSurfaceChanged(width: Int, height: Int) {
            mWidth = width
            mHeight = height
            mState = RenderState.SURFACE_CHANGE
            notifyRender()
        }

        fun onSurfaceDestroyed() {
            mState = RenderState.SURFACE_DESTROY
            notifyRender()
        }

        fun onSurfaceStop() {
            mState = RenderState.STOP
            notifyRender()
        }

        //////////////////////////////////////////// 渲染循环 ////////////////////////////////////////////
        override fun run() {
            // 1.初始化EGL
            initEGL()

            while (true) {
                when (mState) {
                    RenderState.FRESH_SURFACE -> {
                        // 2.使用Surface初始化，并绑定上下文
                        createEGLSurface()
                        waitRender()
                    }
                    RenderState.SURFACE_CHANGE -> {
                        createEGLSurface()
                        // 3.初始化OpenGL世界坐标
                        GLES20.glViewport(0, 0, mWidth, mHeight)
                        configWorldSize()
                        mState = RenderState.RENDERING
                    }
                    RenderState.RENDERING -> {
                        // 4.渲染
                        render()

                        // 如果是 `RENDER_WHEN_DIRTY` 模式，渲染后，把线程挂起，等待下一帧
                        if (mRenderMode == RenderMode.RENDER_WHEN_DIRTY) {
                            waitRender()
                        }
                    }
                    RenderState.SURFACE_DESTROY -> {
                        // 5.销毁Surface 解绑上下文
                        destroyEGLSurface()
                        mState = RenderState.NO_SURFACE
                    }
                    RenderState.STOP -> {
                        // 6.释放资源
                        releaseEGL()
                    }
                    else -> {
                        waitRender()
                    }
                }
                sleep(16)
            }
        }


        private fun initEGL() {
            mEGLSurface = EGLSurfaceHolder()
            mEGLSurface?.init(flags = EGL_RECORDABLE_ANDROID)
        }

        private fun createEGLSurface() {
            if (!mBindedEGLSurface) {
                mBindedEGLSurface = true
                mEGLSurface?.createEGLSurface(mWeakSurfaceView?.get()?.holder?.surface)
                mEGLSurface?.makeCurrent()
                if (!mCreatedEGLContext) {
                    mCreatedEGLContext = true
                    GLES20.glClearColor(0f, 0f, 0f, 0f)
                    GLES20.glEnable(GLES20.GL_BLEND)
                    GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
                    generateTextureID()
                }
            }
        }

        private fun configWorldSize() {
            mDrawers.forEach { it.setWorldSise(mWidth, mHeight) }
        }

        /** 根据渲染模式和当前帧的时间戳判断是否需要重新刷新画面 */
        private fun render() {
            val render = if (mRenderMode == RenderMode.RENDER_CONTINUOUSLY) {
                true
            } else {
                synchronized(mCurTimestamp) {
                    if (mCurTimestamp > mLastTimestamp) {
                        mLastTimestamp = mCurTimestamp
                        true
                    } else {
                        false
                    }
                }
            }
            if (render) {
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
                mDrawers.forEach { it.draw() }
                mEGLSurface?.setTimestamp(mCurTimestamp)
                mEGLSurface?.swapBuffer()
            }
        }

        private fun destroyEGLSurface() {
            mEGLSurface?.destoryEGLSurface()
            mBindedEGLSurface = false
        }

        private fun releaseEGL() {
            mEGLSurface?.release()
        }

        fun setRenderMode(renderMode: RenderMode) {
            mRenderMode = renderMode
        }

        fun notifySwap(timeUs: Long) {

        }

        //////////////////////////////////////////// OpenGL相关 ////////////////////////////////////////////
        private fun generateTextureID() {
            val textureIds = GLTools.createTextureIds(mDrawers.size)
            for ((index, id) in textureIds.withIndex()) {
                mDrawers[index].setTextureID(id)
            }
        }
    }

    enum class RenderState {
        NO_SURFACE,// 没有有效的Surface
        FRESH_SURFACE,// 持有一个新的未初始化的Surface
        SURFACE_CHANGE,// Surface尺寸发生变化
        RENDERING,// 初始化完毕，可以渲染
        SURFACE_DESTROY,// Surface销毁
        STOP,// 停止渲染
    }

    enum class RenderMode {
        RENDER_CONTINUOUSLY,
        RENDER_WHEN_DIRTY
    }
}