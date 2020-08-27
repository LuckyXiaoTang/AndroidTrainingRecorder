package com.zero.tzz.gles3

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.IdRes
import androidx.annotation.RawRes
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.util.*

/**
 *
 * @author Zero_Tzz
 * @date 2020-06-15 16:04
 * @description GLExt
 */

fun Activity.loadRGBAImage(resId: Int, glSurfaceView: SimpleGLSurfaceView) {
    val inputStream = resources.openRawResource(resId)
    val bitmap: Bitmap?
    try {
        bitmap = BitmapFactory.decodeStream(inputStream)
        bitmap?.let {
            val buf = ByteBuffer.allocate(it.byteCount)
            it.copyPixelsToBuffer(buf)
            glSurfaceView.getRender()
                .setImageData(NativeRender.IMAGE_FORMAT_RGBA, it.width, it.height, buf.array())
        }
    } finally {
        try {
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

fun Activity.loadNV21Image(
    assetName: String,
    width: Int,
    height: Int,
    glSurfaceView: SimpleGLSurfaceView
) {
    var inputStream: InputStream? = null
    try {
        inputStream = assets.open(assetName)
        val len = inputStream.available()
        val buffer = ByteArray(len)
        inputStream.read(buffer)
        glSurfaceView.getRender()
            .setImageData(NativeRender.IMAGE_FORMAT_NV21, width, height, buffer)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        try {
            inputStream?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}