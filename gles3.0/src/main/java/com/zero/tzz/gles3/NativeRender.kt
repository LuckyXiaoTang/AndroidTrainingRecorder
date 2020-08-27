package com.zero.tzz.gles3

/**
 *
 * @author Zero_Tzz
 * @date 2020-04-29 14:49
 * @description NativeRender
 */
class NativeRender {

    companion object {
        init {
            System.loadLibrary("native-render")
        }

        public const val IMAGE_FORMAT_RGBA = 0x01
        public const val IMAGE_FORMAT_NV21 = 0x02
        public const val IMAGE_FORMAT_NV12 = 0x03
        public const val IMAGE_FORMAT_I420 = 0x04

        public const val SAMPLE_TYPE = 0
        public const val SAMPLE_TYPE_TRIANGLE = SAMPLE_TYPE
        public const val SAMPLE_TYPE_TEXTURE_MAP = SAMPLE_TYPE + 1
        public const val SAMPLE_TYPE_YUV_TEXTURE_MAP = SAMPLE_TYPE + 2
    }

    external fun native_Init()

    external fun native_UnInit()

    external fun native_OnSurfaceCreated()

    external fun native_OnSurfaceChanged(width: Int, height: Int)

    external fun native_OnDrawFrame()

    external fun native_SetParamsInt(position: Int)

    external fun native_SetImageData(format: Int, width: Int, height: Int, array: ByteArray?)

}