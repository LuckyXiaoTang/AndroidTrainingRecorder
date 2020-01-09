package com.zero.tzz.video.opengl.egl

import android.opengl.EGLContext
import android.opengl.EGLSurface

/**
 *
 * @author Zero_Tzz
 * @date 2019-12-04 10:20
 * @description EGLSurfaceHolder
 */
class EGLSurfaceHolder {
    private val TAG = "EGLSurfaceHolder"

    private lateinit var mEGLCore: EGLCore

    private var mEGLSurface: EGLSurface? = null

    fun init(shareContext: EGLContext? = null, flags: Int) {
        mEGLCore = EGLCore()
        mEGLCore.init(shareContext, flags)
    }

    fun createEGLSurface(surface: Any? = null, width: Int = -1, height: Int = -1) {
        mEGLSurface = if (surface != null) {
            mEGLCore.createWindowSurface(surface)
        } else {
            mEGLCore.createPbufferSurface(width, height)
        }
    }

    fun makeCurrent() {
        mEGLSurface?.let { mEGLCore.makeCurrent(it) }
    }

    fun swapBuffer() {
        mEGLSurface?.let { mEGLCore.swapBuffer(it) }
    }

    fun setTimestamp(timeStamp: Long) {
        mEGLSurface?.let {
            mEGLCore.setPresentationTimestamp(it, timeStamp * 1000)
        }

    }

    fun destoryEGLSurface() {
        mEGLSurface?.let {
            mEGLCore.destoryEGLSurface(it)
            mEGLSurface = null
        }
    }

    fun release() {
        mEGLCore.release()
    }
}