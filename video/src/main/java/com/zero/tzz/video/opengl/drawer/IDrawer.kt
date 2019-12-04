package com.zero.tzz.video.opengl.drawer

import android.graphics.SurfaceTexture

/**
 *
 * @author Zero_Tzz
 * @date 2019-11-29 13:02
 * @description IDrawer
 */
interface IDrawer {

    /** 设置原视频宽高 */
    fun setVideoSize(width: Int, height: Int)

    /** 设置OpenGL窗口宽高 */
    fun setWorldSise(width: Int, height: Int)

    fun setTextureID(id: Int)
    fun draw()
    fun release()
    fun getSurfaceTexture(block: (sf: SurfaceTexture) -> Unit)

    fun alpha(alpha: Float)

    fun scale(scaleX: Float,scaleY: Float)

    fun translate(x: Float, y: Float)
}