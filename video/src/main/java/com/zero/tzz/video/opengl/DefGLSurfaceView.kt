package com.zero.tzz.video.opengl

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import com.zero.tzz.video.opengl.drawer.IDrawer

/**
 *
 * @author Zero_Tzz
 * @date 2019-12-02 15:12
 * @description DefGLSurfaceView
 */
class DefGLSurfaceView : GLSurfaceView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    private var mDrawer: IDrawer? = null

    private var mLastX = 0f
    private var mLastY = 0f

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    mLastX = it.x
                    mLastY = it.y
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (it.x - mLastX) / width
                    val dy = (it.y - mLastY) / height
                    mDrawer?.translate(dx, dy)
                    mLastX = it.x
                    mLastY = it.y
                }
            }
        }
        return true
    }

    fun addDrawer(drawer: IDrawer?) {
        mDrawer = drawer
    }
}