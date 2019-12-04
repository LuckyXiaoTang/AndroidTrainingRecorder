package com.zero.tzz.video.opengl

import android.opengl.GLES20

/**
 *
 * @author Zero_Tzz
 * @date 2019-11-29 13:46
 * @description GLTools
 */
object GLTools {
    fun createTextureIds(count: Int): IntArray {
        val textures = IntArray(count)
        GLES20.glGenTextures(count, textures, 0)
        return textures
    }
}