package com.zero.tzz.gles.render

import android.opengl.GLES20
import android.util.Log
import com.zero.tzz.gles.base.BaseRender
import com.zero.tzz.gles.drawer.IDrawer
import com.zero.tzz.gles.utils.GLTools
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 *
 * @author Zero_Tzz
 * @date 2019-12-10 13:04
 * @description SimpleRender
 */
class SimpleRender : BaseRender() {
    private val TAG = "SimpleRender"
    private var mDrawers = mutableMapOf<String, IDrawer>()
    private var mDrawer: IDrawer? = null

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        Log.d(TAG, "onSurfaceCreated: ")
        GLES20.glClearColor(0f, 0f, 0f, 0f)
        mDrawers.values.forEach { drawer ->
            drawer.setTextureID(GLTools.generateTextureID(1)[0])
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        Log.d(TAG, "onSurfaceChanged: ")
        GLES20.glViewport(0, 0, width, height)
        mDrawers.values.forEach { drawer ->
            drawer.setWorldSize(width, height)
        }
    }

    override fun onDrawFrame(gl: GL10?) {
        Log.d(TAG, "onDrawFrame: $mDrawer")
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        mDrawer?.doDraw()
    }

    fun addDrawers(drawers: Map<String, IDrawer>) {
        mDrawers.putAll(drawers)
    }

    fun updateCurrentDrawer(key: String) {
        mDrawer = mDrawers[key]
        mGLSurfaceView.requestRender()
    }
}