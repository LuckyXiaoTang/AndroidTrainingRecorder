package com.zero.tzz.gles.drawer.simple

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import com.zero.tzz.gles.base.BaseDrawer
import com.zero.tzz.gles.utils.GLHelper
import java.nio.FloatBuffer
import kotlin.math.cos
import kotlin.math.sin

/**
 *
 * @author Zero_Tzz
 * @date 2019-12-16 16:14
 * @description Circular
 */
class Cylinder(context: Context) : BaseDrawer(context) {

    // 圆柱顶点坐标
    private val mVertexCoordinates = mutableListOf<Float>()
    private val mVertexCoordinatesTop = mutableListOf<Float>()
    private val mVertexCoordinatesBottom = mutableListOf<Float>()
    private var mVertexBufferTop: FloatBuffer? = null
    private var mVertexBufferBottom: FloatBuffer? = null
    // 圆柱切片份数（越大 越精细）
    private val N = 360
    // 圆柱 半径
    private val mRadius = 1f
    // 圆柱 高度
    private val HEIGHT = 2f

    override fun initGL() {
        // 3D图形 开启深度测试
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        createVertexCoordinates()
        mVertexBuffer = GLHelper.floatArrayToBuffer(mVertexCoordinates.toFloatArray())
        mVertexBufferTop = GLHelper.floatArrayToBuffer(mVertexCoordinatesTop.toFloatArray())
        mVertexBufferBottom = GLHelper.floatArrayToBuffer(mVertexCoordinatesBottom.toFloatArray())
        mProgram = GLHelper.createProgramFromAssets(
            mContext.resources,
            "simple/vert/cylinder.vert",
            "simple/frag/cylinder.frag"
        )
    }

    private fun createVertexCoordinates() {
        // 圆顶
        mVertexCoordinatesTop.add(0f)
        mVertexCoordinatesTop.add(0f)
        mVertexCoordinatesTop.add(HEIGHT)
        // 圆底
        mVertexCoordinatesBottom.add(0f)
        mVertexCoordinatesBottom.add(0f)
        mVertexCoordinatesBottom.add(0f)
        // 切片坐标
        val span = 360f / N
        var angle = 0f
        while (angle < span + 360f) {
            val x = mRadius * sin(angle * Math.PI / 180f)
            val y = mRadius * cos(angle * Math.PI / 180f)
            var z = HEIGHT
            mVertexCoordinates.add(x.toFloat())
            mVertexCoordinates.add(y.toFloat())
            mVertexCoordinates.add(z)

            mVertexCoordinatesTop.add(x.toFloat())
            mVertexCoordinatesTop.add(y.toFloat())
            mVertexCoordinatesTop.add(z)
            z = 0f
            mVertexCoordinates.add(x.toFloat())
            mVertexCoordinates.add(y.toFloat())
            mVertexCoordinates.add(z)

            mVertexCoordinatesBottom.add(x.toFloat())
            mVertexCoordinatesBottom.add(y.toFloat())
            mVertexCoordinatesBottom.add(z)
            angle += span
        }
    }

    override fun setWorldSize(width: Int, height: Int) {
        mWorldWidth = width
        mWorldHeight = height
        createDefMatrix()
    }

    private fun createDefMatrix() {
        // 计算宽高比 ratio应该需要 < 1
        val ratio: Float
        if (mWorldWidth > mWorldHeight) {
            ratio = mWorldHeight.toFloat() / mWorldWidth
            // 设置透视投影 以宽为基准（max = 1）
            Matrix.frustumM(mProjectMatrix, 0, -1f, 1f, -ratio, ratio, 3f, 20f)
            Matrix.setLookAtM(mViewMatrix, 0, 1.0f, -10.0f, -4.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0)
        } else {
            ratio = mWorldWidth.toFloat() / mWorldHeight
            // 设置透视投影 以高为基准（max = 1）
            Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 20f)
            Matrix.setLookAtM(mViewMatrix, 0, 1.0f, -10.0f, -4.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0)
        }
    }

    override fun doDraw() {
        GLES20.glUseProgram(mProgram)
        mPositionHandler = GLES20.glGetAttribLocation(mProgram, "vPosition")
        mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix")
        GLES20.glEnableVertexAttribArray(mPositionHandler)
        GLES20.glVertexAttribPointer(
            mPositionHandler,
            3,
            GLES20.GL_FLOAT,
            false,
            3 * 4,
            mVertexBuffer
        )
        GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mMVPMatrix, 0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, mVertexCoordinates.size / 3)
        // 重新绑定 三角形坐标
        GLES20.glVertexAttribPointer(
            mPositionHandler,
            3,
            GLES20.GL_FLOAT,
            false,
            3 * 4,
            mVertexBufferTop
        )
        GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mMVPMatrix, 0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, mVertexCoordinatesTop.size / 3)
        // 重新绑定 三角形坐标
        GLES20.glVertexAttribPointer(
            mPositionHandler,
            3,
            GLES20.GL_FLOAT,
            false,
            3 * 4,
            mVertexBufferBottom
        )
        GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mMVPMatrix, 0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, mVertexCoordinatesBottom.size / 3)
    }

    override fun release() {
        GLES20.glDisableVertexAttribArray(mPositionHandler)
    }
}