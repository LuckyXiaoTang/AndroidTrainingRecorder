package com.topvision.media.egl

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.EGL14
import android.opengl.GLES20
import android.view.*
import java.lang.ref.WeakReference

/**
 *
 * @author Zero_Tzz
 * @date 2020-05-12 16:27
 * @description EGLRender
 */
class EGLRender : SurfaceHolder.Callback, TextureView.SurfaceTextureListener,
    View.OnAttachStateChangeListener {
    /** OpenGL渲染线程 */
    private val mRenderThread = RenderThread()

    private var mWeakSurface: WeakReference<Surface>? = null

    private val mDrawers = mutableListOf<IDrawer>()

    init {
        mRenderThread.start()
    }

    fun setRenderView(surface: Any?) {
        if (surface == null) throw Throwable("Invalid RenderView")
        when (surface) {
            is SurfaceView -> {
                val holder = surface.holder
                mWeakSurface = WeakReference(holder.surface)
                holder.addCallback(this)
                surface.addOnAttachStateChangeListener(this)
            }
            is TextureView -> {
                surface.surfaceTextureListener = this
                surface.addOnAttachStateChangeListener(this)
            }
        }
    }

    fun setRenderMode(renderMode: RenderMode) {
        mRenderThread.setRenderMode(renderMode)
    }

    fun addDrawer(drawer: IDrawer) {
        mDrawers.add(drawer)
    }

    fun stop() {
        mRenderThread.onSurfaceStop()
    }

    //////////////////////////////////////////// SurfaceView ////////////////////////////////////////////
    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        mRenderThread.onSurfaceChanged(width, height)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        mRenderThread.onSurfaceDestroy()
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        mRenderThread.onSurfaceCreated()
    }

    //////////////////////////////////////////// TextureView ////////////////////////////////////////////
    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
        mRenderThread.onSurfaceChanged(width, height)
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {

    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        mRenderThread.onSurfaceDestroy()
        return true
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
        mWeakSurface = WeakReference(Surface(surface))
        mRenderThread.onSurfaceChanged(width,height)
    }

    //////////////////////////////////////////// Window ////////////////////////////////////////////
    override fun onViewDetachedFromWindow(v: View?) {
        mRenderThread.onSurfaceStop()
    }

    override fun onViewAttachedToWindow(v: View?) {

    }


    inner class RenderThread : Thread() {

        private var mRenderMode = RenderMode.RENDER_WHEN_DIRTY

        private var mRenderState = RenderState.NO_SURFACE

        private lateinit var mEGLSurface: EGLSurfaceHolder

        private var mBindedSurface = false

        private var mCreatedEGLContext = false

        private var mWidth = 0

        private var mHeight = 0

        private var mCurTimestamp = 0L

        private var mLastTimestamp = 0L

        private val mLock = Object()
        override fun run() {
            initEGL()
            while (true) {
                when (mRenderState) {
                    RenderState.NO_SURFACE -> {
                        waitRender()
                    }
                    RenderState.NEW_SURFACE -> {
                        createEGLSurface()
                        waitRender()
                    }
                    RenderState.SURFACE_CHANGE -> {
                        createEGLSurface()
                        GLES20.glViewport(0, 0, mWidth, mHeight)
                        mDrawers.forEach { drawer -> drawer.setWorldSise(mWidth, mHeight) }
                        mRenderState = RenderState.RENDERING
                    }
                    RenderState.RENDERING -> {
                        render()
                        // 如果是 `RENDER_WHEN_DIRTY` 模式，渲染后，把线程挂起，等待下一帧
                        if (mRenderMode == RenderMode.RENDER_WHEN_DIRTY) waitRender()
                    }
                    RenderState.SURFACE_DESTROY -> {
                        destroyEGLSurface()
                        mRenderState = RenderState.NO_SURFACE
                    }
                    RenderState.STOP -> {
                        release()
                    }
                }
            }

        }

        private fun initEGL() {
            mEGLSurface = EGLSurfaceHolder()
            mEGLSurface.init(flag = EGL_RECORDABLE_ANDROID)
        }

        private fun createEGLSurface() {
            if (!mBindedSurface) {
                mBindedSurface = true
                mEGLSurface.createEGLSurface(mWeakSurface?.get())
                mEGLSurface.makeCurrent()
                if (!mCreatedEGLContext) {
                    mCreatedEGLContext = true
                    GLES20.glClearColor(0f, 0f, 0f, 0f)
                    GLES20.glEnable(GLES20.GL_BLEND)
                    GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
                    // 绑定TextureId
                    val textureIds = GLTools.createTextureIds(mDrawers.size)
                    for ((index, id) in textureIds.withIndex()) {
                        mDrawers[index].setTextureID(id)
                    }
                }
            }
        }

        private fun render() {
            val render = if (mRenderMode == RenderMode.RENDER_CONTINUOUSLY) {
                true
            } else {
                synchronized(mCurTimestamp) {
                    if (mCurTimestamp >= mLastTimestamp) {
                        mLastTimestamp = mCurTimestamp
                        true
                    } else {
                        false
                    }
                }
            }
            if (render) {
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
                mDrawers.forEach { drawer -> drawer.draw() }
                mEGLSurface.setTimestamp(mCurTimestamp)
                mEGLSurface.swapBuffer()
            }
        }

        private fun destroyEGLSurface() {
            mEGLSurface.destroySurface()
            mBindedSurface = false
        }

        private fun release() {
            mEGLSurface.release()
        }

        private fun waitRender() {
            synchronized(mLock) { mLock.wait() }

        }

        private fun notifyRender() {
            synchronized(mLock) { mLock.notifyAll() }
        }

        fun setRenderMode(renderMode: RenderMode) {
            mRenderMode = renderMode
        }

        fun onSurfaceCreated() {
            mRenderState = RenderState.NEW_SURFACE
            notifyRender()
        }

        fun onSurfaceChanged(width: Int, height: Int) {
            mWidth = width
            mHeight = height
            mRenderState = RenderState.SURFACE_CHANGE
            notifyRender()
        }

        fun onSurfaceDestroy() {
            mRenderState = RenderState.SURFACE_DESTROY
            notifyRender()
        }

        fun onSurfaceStop() {
            mRenderState = RenderState.STOP
            notifyRender()
        }
    }

    enum class RenderState {
        NO_SURFACE,
        NEW_SURFACE,
        SURFACE_CHANGE,
        RENDERING,
        SURFACE_DESTROY,
        STOP
    }

    enum class RenderMode {
        RENDER_CONTINUOUSLY,
        RENDER_WHEN_DIRTY
    }
}