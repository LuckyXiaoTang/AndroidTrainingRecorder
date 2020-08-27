package com.zero.tzz.libcamera

import android.graphics.SurfaceTexture
import android.transition.Scene
import android.transition.TransitionManager
import androidx.annotation.IntDef
import java.lang.annotation.RetentionPolicy

/**
 *
 * @author Zero_Tzz
 * @date 2020-05-18 09:35
 * @description CameraCompat
 */
abstract class CameraCompat {

    companion object {
        public const val FRONT_CAMERA = 1

        public const val BACK_CAMERA = 2
    }

    @IntDef(FRONT_CAMERA, BACK_CAMERA)
    @Retention(AnnotationRetention.SOURCE)
    public annotation class CameraType


    protected var mSurfaceTexture: SurfaceTexture? = null

    fun a() {

    }
}