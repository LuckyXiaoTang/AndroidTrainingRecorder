package com.zero.tzz.gles.base

import android.opengl.GLSurfaceView

/**
 *
 * @author Zero_Tzz
 * @date 2019-12-10 11:19
 * @description BaseRender
 */
abstract class BaseRender : GLSurfaceView.Renderer {

    protected lateinit var mGLSurfaceView: GLSurfaceView

    fun attachGLSurfaceView(view: GLSurfaceView) {
        mGLSurfaceView = view
    }
}