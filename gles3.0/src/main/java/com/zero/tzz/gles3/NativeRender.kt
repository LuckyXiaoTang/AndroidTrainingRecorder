package com.zero.tzz.gles3

/**
 *
 * @author Zero_Tzz
 * @date 2020-04-29 14:49
 * @description NativeRender
 */
class NativeRender {

    companion object{
        init {
            System.loadLibrary("native_render")
        }
    }

    external fun native_Init()

    external fun native_UnInit()

    external fun native_OnDrawFrame()

    external fun native_OnSurfaceChanged(width: Int, height: Int)

    external fun native_OnSurfaceCreated()

}