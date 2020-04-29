package com.zero.tzz.gles.utils

import android.opengl.GLES20

/**
 *
 * @author Zero_Tzz
 * @date 2019-12-10 13:33
 * @description GLTools
 */
object GLTools {

    fun generateTextureID(count: Int): IntArray {
        val ids = IntArray(count)
        GLES20.glGenTextures(count, ids, 0)
        return ids
    }
}