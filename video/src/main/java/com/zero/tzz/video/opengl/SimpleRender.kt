package com.zero.tzz.video.opengl

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.zero.tzz.video.opengl.drawer.IDrawer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 *
 * @author Zero_Tzz
 * @date 2019-11-29 13:12
 * @description SimpleRender
 */
class SimpleRender : GLSurfaceView.Renderer {

    private val mDrawers = mutableListOf<IDrawer>()

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 0f)
        // 开启透明度混合模式
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        val textureIds = GLTools.createTextureIds(mDrawers.size)
        for ((index, id) in textureIds.withIndex()) {
            mDrawers[index].setTextureID(id)
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        mDrawers.forEach { it.setWorldSise(width, height) }
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        mDrawers.forEach { it.draw() }
    }

    fun addDrawer(drawer: IDrawer) {
        mDrawers.add(drawer)
    }
}