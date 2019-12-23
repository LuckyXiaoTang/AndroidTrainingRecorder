package com.zero.tzz.gles.drawer

/**
 *
 * @author Zero_Tzz
 * @date 2019-12-10 13:27
 * @description IDrawer
 */
interface IDrawer {

    fun setTextureID(id: Int)

    fun setWorldSize(width: Int, height: Int)

    fun doDraw()

    fun release()
}