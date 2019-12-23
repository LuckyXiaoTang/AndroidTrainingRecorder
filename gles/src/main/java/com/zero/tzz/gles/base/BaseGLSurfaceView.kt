package com.zero.tzz.gles.base

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

/**
 *
 * @author Zero_Tzz
 * @date 2019-12-10 11:17
 * @description BaseGLSurfaceView
 */
abstract class BaseGLSurfaceView : GLSurfaceView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
}