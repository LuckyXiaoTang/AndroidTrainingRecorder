package com.topvision.media.egl

import android.opengl.EGLContext
import android.opengl.EGLSurface
import android.view.Surface

/**
 *
 * @author Zero_Tzz
 * @date 2020-05-12 16:20
 * @description EGLSurfaceHolder
 */
class EGLSurfaceHolder {
    private val TAG = "EGLSurfaceHolder"

    private lateinit var mEGLCore: EGLCore

    private var mEGLSurface: EGLSurface? = null

    fun init(eglContext: EGLContext? = null, flag: Int) {
        mEGLCore = EGLCore()
        mEGLCore.init(eglContext, flag)
    }

    fun createEGLSurface(surface: Any? = null, width: Int = -1, height: Int = -1) {
        mEGLSurface = surface?.let { mEGLCore.createWindowSurface(surface) }
            ?: mEGLCore.createPbufferSurface(width, height)
    }

    fun makeCurrent() {
        mEGLSurface?.let { mEGLCore.makeCurrent(it) }

    }

    fun swapBuffer() {
        mEGLSurface?.let { mEGLCore.swapBuffer(it) }
    }

    fun setTimestamp(timestamp: Long) {
        mEGLSurface?.let { mEGLCore.setPresentationTimestamp(it, timestamp) }
    }

    fun destroySurface() {
        mEGLSurface?.let { mEGLCore.destroySurface(it) }
        mEGLSurface = null
    }

    fun release() {
        mEGLCore.release()
    }
}