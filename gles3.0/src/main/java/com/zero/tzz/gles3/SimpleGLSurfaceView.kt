package com.zero.tzz.gles3

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 *
 * @author Zero_Tzz
 * @date 2020-04-29 14:46
 * @description SimpleGLSurfaceView
 */
class SimpleGLSurfaceView : GLSurfaceView {
    private val TAG = "SimpleGLSurfaceView"
    private var mNativeRender: NativeRender
    private var mGLRender: SimpleGLRender


    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    init {
        setEGLContextClientVersion(3)
        mNativeRender = NativeRender()
        mGLRender = SimpleGLRender(mNativeRender)
        setRenderer(mGLRender)
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    inner class SimpleGLRender(var mNativeRender: NativeRender) : Renderer {
        override fun onDrawFrame(gl: GL10?) {
            Log.d(TAG, "onDrawFrame() called with: gl = [$gl]")
            mNativeRender.native_OnDrawFrame()
        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            Log.d(TAG, "onSurfaceChanged() called with: gl = [$gl],  width = [$width], height = [$height]")
            mNativeRender.native_OnSurfaceChanged(width, height)
        }

        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            Log.d(TAG, "onSurfaceCreated() called with: gl = [$gl], config = [$config]")
            mNativeRender.native_OnSurfaceCreated()
        }
    }
}